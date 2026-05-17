import {SpinnerOnButton} from './component/button.spinner.component.js';
import {
    apiFetch,
    copyToClipboard,
    getCookie,
    getQueryVariable,
    getUserLanguage,
    initTools,
    setCookie
} from "./lib/tools.js";
import './component/icon.component.js';
import {i18n} from "./lib/i18n.js";
import Modal from "./component/modal.component.js";

let passwordRegex = null;
let jwtTokenRecovery = null;

document.addEventListener('DOMContentLoaded', async function () {
    initTools();
    // Clean lastState
    sessionStorage.removeItem('lastState');

    // Attempt auto login
    await autoLogin();

    // Last login
    if (localStorage.getItem("lastUsername")) {
        document.getElementById("username").value = localStorage.getItem("lastUsername");
        document.getElementById("forgot-password-username").value = localStorage.getItem("lastUsername");
    }

    // Last host
    lastHost();

    // Got here from invitation link
    if (getQueryVariable('register')) {
        document.getElementById('register-invitation').value = getQueryVariable('register') ? getQueryVariable('register') : "";
        switchToRegister();
    }

    await i18n.translate(getUserLanguage());

    document.getElementById("register-host").onchange = () => { getHostSettings() }
    document.getElementById("login-button").onclick = userLogin
    document.getElementById("forgot-password-button").onclick = forgotPassword
    document.getElementById("switch-to-register-button").onclick = switchToRegister
    document.getElementById("forget-password-button").onclick = switchToForgetPassword
    document.getElementById("back-to-login-button").onclick = switchToLogin
    document.getElementById("user-register-button").onclick = userRegister
    document.getElementById("switch-to-login-button").onclick = switchToLogin
    document.getElementById("register-password").oninput = () => { passwordValidator(document.getElementById("register-password"), false); }
    document.getElementById("register-password-confirm").oninput = () => { passwordValidator(document.getElementById("register-password-confirm"), true); }
    document.getElementById("reset-password-button").onclick = resetPassword
});

document.getElementById("login-form").addEventListener('keydown', function (e) {
    if (e.key === 'Enter') {
        userLogin();
    }
});

function userLogin() {
    const FORM = document.getElementById("login-form");
    const LOGIN = {
        'username': FORM.username.value,
        'password': FORM.password.value,
    };

    // Validate URL
    try {
        const inputHost = new URL(FORM.host.value);
        login(LOGIN, inputHost.origin);
    }
    catch (e) {
        Modal.toggleError(i18n.translateOne("login.error"), e.message);
    }
}

async function forgotPassword() {
    const FORM = document.getElementById("forgot-password-form");
    const LOGIN = {
        'username': FORM.username.value,
        'code': FORM["recovery-code"].value,
    };

    // Validate URL
    const host = new URL(FORM.host.value).origin;
    const spinner = new SpinnerOnButton("forgot-password-button")
    try {
        spinner.run()
        const response = await apiFetch(`${host}/api/auth/login/recovery-codes`, {
            cache: "no-store",
            signal: AbortSignal.timeout(5000),
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST',
            body: JSON.stringify(LOGIN),
        });

        if (response.ok) {
            const jwtToken = await response.text();
            const totp = response.headers.get('X-Totp-Active');
            spinner.success()
            if (totp) {
                await toggleTOTPValidation(host, LOGIN.username, jwtToken, 'auth/login/recovery-codes/totp', goToResetPassword)
            } else {
                goToResetPassword(host, LOGIN.username, jwtToken)
            }
        } else {
            spinner.error()
            const message = await response.text();
            await Modal.toggleError(i18n.translateOne("login.error"), message);
        }
    } catch (error) {
        spinner.error()
        await Modal.toggle({
            icon: "error",
            title: i18n.translateOne("login.error.host", host),
            text: error.message,
            allowOutsideClick: false,
        })
    }
}

function goToResetPassword(host, username, jwtToken) {
    localStorage.setItem("lastHost", host);
    localStorage.setItem("lastUsername", username);
    jwtTokenRecovery = jwtToken;
    switchToResetPassword()
}

async function resetPassword() {
    const FORM = document.getElementById("reset-password-form");

    // Validate URL
    const host = localStorage.getItem("lastHost");
    const spinner = new SpinnerOnButton("reset-password-button")
    try {
        spinner.run()
        const response = await apiFetch(`${host}/api/auth/login/new-password`, {
            cache: "no-store",
            signal: AbortSignal.timeout(5000),
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${jwtTokenRecovery}`
            },
            method: 'POST',
            body: JSON.stringify({'password': FORM.password.value, 'confirmPassword': FORM.passwordConfirm.value}),
        });

        if (response.ok) {
            localStorage.setItem("lastHost", host);
            jwtTokenRecovery = null;
            spinner.success()
            await Modal.toggle({icon: "success", title: i18n.translateOne('login.reset.password.success')})
            switchToLogin()
        } else {
            spinner.error()
            const message = await response.text();
            await Modal.toggleError(i18n.translateOne("login.error"), message);
        }
    } catch (error) {
        await Modal.toggle({
            icon: "error",
            title: i18n.translateOne("login.error.host", host),
            text: error.message,
            allowOutsideClick: false,
        })
    }
}

async function login(loginData, host) {
    const spinner = new SpinnerOnButton("login-button")
    try {
        spinner.run()
        const response = await apiFetch(`${host}/api/auth/login`, {
            cache: "no-store",
            signal: AbortSignal.timeout(5000),
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'POST',
            body: JSON.stringify(loginData),
        });

        if (response.ok) {
            const jwtToken = await response.text();
            const totp = response.headers.get('X-Totp-Active');
            spinner.success()
            if (totp) {
                await toggleTOTPValidation(host, loginData.username, jwtToken, 'auth/login/totp', loginSuccess)
            } else {
                loginSuccess(host, loginData.username, jwtToken)
            }
        } else {
            spinner.error()
            const message = await response.text();
            await Modal.toggleError(i18n.translateOne("login.error.host"), message);
        }
    }
    catch (error) {
        console.error(error.name);
        console.error(error.message);
        console.error(error.cause);
        console.error(error.stack);
        spinner.error()
        Modal.toggle({
            icon: "error",
            title: i18n.translateOne("login.error.host", host),
            text: error.message,
            allowOutsideClick: false,
        })
    }
}

function loginSuccess(host, username, jwtToken) {
    localStorage.setItem("lastHost", host);
    localStorage.setItem("lastUsername", username);
    setCookie('jwtToken', jwtToken, 1);
    document.location.href = `app.html`;
}

/**
 * @param {string} host
 * @param {string} username
 * @param {string} jwtToken
 * @param {string} url
 * @param {(host:string, username:string, jwtToken:string) => void} totpSuccessCallback
 * @param {boolean} error
 */
async function toggleTOTPValidation(host, username, jwtToken, url, totpSuccessCallback, error = false) {
    let code = ''
    await Modal.toggle({
        icon: error ? "error" : "success",
        showCancelButton: true,
        html: `<div data-i18n="login.register.totp.code">Enter your OpenID code</div>
               <form class="popup" style="display: flex; flex-direction: column; background-color: var(--pri-bg-color); padding: 1rem; margin: 1rem;">
                   <input type="text" name="password" id="totp-code">
               </form>`,
        width: '30rem',
        didOpen: async () => {
            const select = document.getElementById('totp-code');
            select.oninput = () => { code = select.value };
        },
        allowOutsideClick: false,
    }).then(async (result) => {
        const totpData = {
            username: username,
            code: code
        }
        if (result.isConfirmed) {
            const response = await apiFetch(`${host}/api/${url}`, {
                cache: "no-store",
                signal: AbortSignal.timeout(5000),
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${jwtToken}`
                },
                method: 'POST',
                body: JSON.stringify(totpData),
            });
            if (response.ok) {
                const authToken = await response.text()
                totpSuccessCallback(host, username, authToken)
            } else {
                await toggleTOTPValidation(host, username, url, jwtToken, true)
            }
        }
    })
}

function lastHost() {
    if (localStorage.getItem("lastHost")) {
        document.getElementById("login-form").host.value = localStorage.getItem("lastHost");
        document.getElementById("register-form").host.value = localStorage.getItem("lastHost");
        document.getElementById("forgot-password-form").host.value = localStorage.getItem("lastHost");
        getHostSettings();
    }
    else if (document.location.hostname !== "localhost") {
        document.getElementById("login-form").host.value = document.location.origin;
        document.getElementById("register-form").host.value = document.location.origin;
        document.getElementById("forgot-password-form").host.value = document.location.origin;
        getHostSettings();
    }
}

function switchToRegister() {
    document.getElementById('login-form').classList.add("hidden");
    document.getElementById('forgot-password-form').classList.add("hidden");
    document.getElementById('register-form').classList.remove("hidden");
    document.getElementById('reset-password-form').classList.add("hidden");
}

function switchToForgetPassword() {
    document.getElementById('forgot-password-form').classList.remove("hidden");
    document.getElementById('login-form').classList.add("hidden");
    document.getElementById('register-form').classList.add("hidden");
    document.getElementById('reset-password-form').classList.add("hidden");
}

function switchToLogin() {
    document.getElementById('login-form').classList.remove("hidden");
    document.getElementById('forgot-password-form').classList.add("hidden");
    document.getElementById('register-form').classList.add("hidden");
    document.getElementById('reset-password-form').classList.add("hidden");
}

function switchToResetPassword() {
    document.getElementById('login-form').classList.add("hidden");
    document.getElementById('forgot-password-form').classList.add("hidden");
    document.getElementById('register-form').classList.add("hidden");
    document.getElementById('reset-password-form').classList.remove("hidden");
}

function userRegister() {
    const FORM = document.getElementById("register-form");

    if (!FORM.username.value) {
        Modal.toggleError(i18n.translateOne('login.register.username.error'));
        return;
    }

    if (FORM.password.value !== FORM.passwordConfirm.value) {
        Modal.toggle({
            icon: 'error',
            title: i18n.translateOne('login.register.password.match.error'),
            showCancelButton: false,
        });
        return;
    }

    const REGISTER = {
        'username': FORM.username.value,
        'password': FORM.password.value,
        'email': null,
        'invitationLink': FORM.invitation.value
    };

    // Validate URL
    try {
        const inputHost = new URL(FORM.host.value);
        register(REGISTER, inputHost.origin);
    }
    catch (e) {
        Modal.toggle({
            icon: 'error',
            title: i18n.translateOne('login.register.error'),
            text: e.message,
            showCancelButton: false,
        });
    }
}

async function register(loginData, host) {
    try {
        const response = await apiFetch(`${host}/api/auth/signup`, {
            cache: "no-store",
            signal: AbortSignal.timeout(5000),
            headers: {
                'Content-Type': 'application/json'
            },
            method: 'PUT',
            body: JSON.stringify(loginData),
        });

        if (!response.ok) {
            throw new Error("Not OK");
        }

        const result = /** @type {NewUserRepresentation} */ await response.json();

        Modal.toggle({
            icon: "success",
            title: i18n.translateOne('login.register.success', host),
            html: `<div data-i18n="login.register.success.recover.codes">your recover codes</div>
                   <div style="background-color: var(--pri-bg-color); padding: 1rem; margin: 1rem;">
                       <div class="icon" id="recover-codes-clip" style="position: absolute; cursor: pointer;">
                           <revoice-icon-clipboard></revoice-icon-clipboard>
                       </div>
                       <code id="recover-codes"></code>
                   </div>`,
            didOpen: async () => {
                const codes = document.getElementById('recover-codes');
                codes.innerText = result.recoverCodes.join('\n')
                const clipButton = document.getElementById('recover-codes-clip');
                clipButton.onclick = () => {
                    copyToClipboard(result.recoverCodes.join('\n'))
                }
            },
            allowOutsideClick: false,
        }).then(() => {
            document.location.reload();
        })
    }
    catch (error) {
        Modal.toggle({
            icon: "error",
            title: i18n.translateOne('login.register.host.error', host),
            text: error.message,
            allowOutsideClick: false,
        })
    }
}

function passwordValidator(element, udpateButton) {
    const button = document.getElementById('user-register-button');
    if (passwordRegex?.test(element.value)) {
        element.classList.remove('password-reject');
        element.classList.add('password-accept');
        if (udpateButton) {
            button.disabled = false;
        }
    }
    else {
        element.classList.remove('password-accept');
        element.classList.add('password-reject');
        if (udpateButton) {
            button.disabled = true;
        }
    }
}

async function autoLogin() {
    const storedToken = getCookie('jwtToken');
    const storedCoreUrl = localStorage.getItem("lastHost");
    if (storedToken && storedCoreUrl) {
        try {
            const response = await apiFetch(`${storedCoreUrl}/api/user/me`, {
                cache: "no-store",
                signal: AbortSignal.timeout(5000),
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${storedToken}`
                },
                method: 'GET',
            });

            if (response.ok) {
                document.location.href = `app.html`;
            }
        }
        catch (error) {
            console.error(error);
        }
    }
}

async function getHostSettings() {
    try {
        const host = new URL(document.getElementById("register-host").value);
        const response = await apiFetch(`${host.origin}/api/settings`, {
            cache: "no-store",
            signal: AbortSignal.timeout(5000),
            headers: {
                'Content-Type': 'application/json',
            },
            method: 'GET',
        });

        const result = await response.json();

        // Show / Hide invitation input
        if (result["global.app-only-accessible-by-invitation"] == "true") {
            document.getElementById("register-invitation-div").classList.remove("hidden");
        }
        else{
            document.getElementById("register-invitation-div").classList.add("hidden");
        }

        // Build password Regex
        let pattern = "";
        pattern += `(?=.*[!@#$%^&.*]{${result["global.password.min-special-char"]},})`;
        pattern += `(?=.*[0-9]{${result["global.password.min-number"]},})`;
        pattern += `(?=.*[A-Z]{${result["global.password.min-uppercase"]},})`;
        pattern += `(?=.*[a-z]{${result["global.password.min-lowercase"]},})`;
        pattern += `[a-zA-Z0-9!@#$%^&.*]{${result["global.password.min-length"]},}$`;
        passwordRegex = new RegExp(pattern);
    }
    catch (error) {
        console.error(error);
    }
}