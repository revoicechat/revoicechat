const tauriActive = globalThis.isTauri;
let tauriFetch = null;

function initTools() {
    if (tauriActive && !import.meta.env?.VITEST) {
        // Use string concatenation to hide the import from Vite's static analysis
        const moduleName = '@tauri-apps/' + 'plugin-http';
        import(/* @vite-ignore */ moduleName)
            .then(module => {
                tauriFetch = module.fetch;
            })
            .catch(() => {
                console.warn('Tauri HTTP plugin not available, using standard fetch');
                tauriFetch = globalThis.fetch;
            });
    } else {
        // Use standard fetch in tests
        tauriFetch = globalThis.fetch;
    }
}
/**
 * @param {string} str
 * @return {string}
 */
const sanitizeString = (str) => str.substring(0, 2000).trim();


/**
 * Is the date today
 * @param {Date} date Date to check
 * @returns boolean
 */
function isToday(date) {
    const today = new Date();
    return today.getFullYear() === date.getFullYear() && today.getMonth() === date.getMonth() && today.getDate() === date.getDate();
}

/**
 * Convert UNIX timestamp to readable Date:Hour format
 * @param {*} timestamp
 * @returns string
 */
function timestampToText(timestamp) {
    // By default timestamp is UTC (shouldn't matter for this function)
    timestamp = new Date(`${timestamp}`);
    let formatedTimestamp = timestamp.toLocaleString();
    formatedTimestamp = formatedTimestamp.substring(0, formatedTimestamp.length - 3);
    // Is today ?
    if (isToday(timestamp)) {
        formatedTimestamp = String(timestamp.getHours()).padStart(2, '0') + ":" + String(timestamp.getMinutes()).padStart(2, '0');
    }
    return formatedTimestamp;
}

/**
 * Retrieve query variable from URL
 * @param {string} variable Name of variable
 * @returns {string|null} Value of variable
 */
function getQueryVariable(variable) {
    const query = globalThis.location.search.substring(1);
    const vars = query.split("&");
    for (const element of vars) {
        const pair = element.split("=");
        if (pair[0] === variable) {
            return pair[1];
        }
    }
    return null;
}

/**
 * Set a cookie to browser / tauri
 * @param {string} name Name of cookie
 * @param {string} value Data of cookie
 * @param {number} days Expiration (in days)
 */
function setCookie(name, value, days) {
    if (tauriActive) {
        const data = {
            value: value,
            expires: days ? Date.now() + (days * 24 * 60 * 60 * 1000) : null
        };
        localStorage.setItem(`secure_${name}`, JSON.stringify(data));
    } else {
        let expires = "";
        if (days) {
            const date = new Date();
            date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
            expires = "; expires=" + date.toUTCString();
        }
        document.cookie = name + "=" + encodeURIComponent(value) + expires + "; path=/; SameSite=Strict";
    }
}

/**
 * Get a cookie from browser / tauri
 * @param {string} name Name of cookie to read/get
 * @returns {string|null} Data of cookie
 */
function getCookie(name) {
    if (tauriActive) {
        const stored = localStorage.getItem(`secure_${name}`);
        if (!stored) return null;

        try {
            const data = JSON.parse(stored);
            if (data.expires && Date.now() > data.expires) {
                localStorage.removeItem(`secure_${name}`);
                return null;
            }

            return data.value;
        } catch (e) {
            console.error('Error parsing stored data:', e);
            return null;
        }
    } else {
        const nameEQ = name + "=";
        const cookies = document.cookie.split(';');
        for (let c of cookies) {
            let cookie = c.trim();
            if (cookie.startsWith(nameEQ)) {
                return decodeURIComponent(cookie.substring(nameEQ.length));
            }
        }
        return null;
    }
}

/**
 * Erase a named cookie
 * @param name Name of the cookie
 */
function eraseCookie(name) {
    if (tauriActive) {
        localStorage.removeItem(`secure_${name}`);
    } else {
        document.cookie = name + "=; Max-Age=-99999999; path=/";
    }
}

/**
 * Copy data to user clipboard
 * When available it use the newest API clipboard.writeText()
 * @param {string} data Data to copy to user clipboard
 */
async function copyToClipboard(data) {
    try {
        if (navigator.clipboard) {
            await navigator.clipboard.writeText(data);
        } else {
            // Fallback
            const input = document.createElement('input');
            input.id = 'input-copy'
            input.value = data;
            document.body.appendChild(input);
            document.getElementById('input-copy').select();
            document.execCommand("copy"); // NOSONAR - it is a fallback method
            input.remove();
        }
    } catch (err) {
        console.error('copyToClipboard: Failed to copy:', err);
    }
}

/**
 * Format bytes as human-readable text.
 *
 * @param {number} bytes Number of bytes.
 * @param {boolean} si True to use metric (SI) units, aka powers of 1000. False to use binary (IEC), aka powers of 1024.
 * @param {number} dp Number of decimal places to display.
 *
 * @return {string} Formatted string.
 */
function humanFileSize(bytes, si = false, dp = 1) {
    const thresh = si ? 1000 : 1024;

    if (Math.abs(bytes) < thresh) {
        return bytes + ' B';
    }

    const units = si
        ? ['kB', 'MB', 'GB', 'TB', 'PB', 'EB', 'ZB', 'YB']
        : ['KiB', 'MiB', 'GiB', 'TiB', 'PiB', 'EiB', 'ZiB', 'YiB'];
    let u = -1;
    const r = 10 ** dp;

    do {
        bytes /= thresh;
        ++u;
    } while (Math.round(Math.abs(bytes) * r) / r >= thresh && u < units.length - 1);

    return bytes.toFixed(dp) + ' ' + units[u];
}

/**
 * Convert user status to className
 * @param {string} status User status
 * @returns {"background-green"|"background-orange"|"background-red"|"background-gray"} Corresponding className
 */
function statusToColor(status) {
    switch (status) {
        case "ONLINE":
            return "green";
        case "AWAY":
            return "orange";
        case "DO_NOT_DISTURB":
            return "red";
        case "INVISIBLE":
        default:
            return "gray";
    }
}

/**
 * Fetch wrapper for Tauri
 * @param url
 * @param options
 * @returns {Promise<Response>} function to use
 */
async function apiFetch(url, options = {}) {
    if (tauriActive && tauriFetch) {
        return tauriFetch(url, options);
    }
    return fetch(url, options);
}

function getUserLanguage() {
  const lang = navigator.language || navigator.languages?.[0] || 'en';
  return lang.split('-')[0];
}

export {
    initTools,
    tauriActive,
    sanitizeString,
    timestampToText,
    getQueryVariable,
    setCookie,
    getCookie,
    eraseCookie,
    copyToClipboard,
    humanFileSize,
    statusToColor,
    apiFetch,
    getUserLanguage
};
