/** @param {string} s */
export function isUUID(s) {
    return /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i.test(s);
}

function isGifUrl(message) {
    if (typeof message !== 'string') return false;
    const gifHostRegex = /^https?:\/\/([\w-]+\.)*(giphy\.com|tenor\.com|klipy\.co|klipy\.com)\//i;
    return gifHostRegex.test(message.trim());
}

function isOnlyGifUrl(message) {
    if (typeof message !== 'string') return false;
    const trimmed = message.trim();
    if (/\s/.test(trimmed)) return false;
    return isGifUrl(trimmed);
}

function looksLikeImageUrl(url) {
    if (typeof url !== 'string') return false;
    try {
        const { pathname } = new URL(url);
        return /\.(gif|webp|png|jpe?g|mp4)$/i.test(pathname);
    } catch {
        return false;
    }
}

export function isValidGifUrl(message) {
    return isOnlyGifUrl(message) && looksLikeImageUrl(message);
}