import { SpinnerOnButton } from './component/button.spinner.component.js';
import {apiFetch, getCookie, getQueryVariable, getUserLanguage, initTools, setCookie} from "./lib/tools.js";
import './component/icon.component.js';
import { i18n } from "./lib/i18n.js";
import Modal from "./component/modal.component.js";

let passwordRegex = null;

document.addEventListener('DOMContentLoaded', async function () {
    initTools();
    // Clean lastState
    sessionStorage.removeItem('lastState');

    // Attempt auto login
    await autoLogin();

    // Last login
    if (localStorage.getItem("lastUsername")) {
        document.getElementById("username").value = localStorage.getItem("lastUsername");
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
    document.getElementById("switch-to-register-button").onclick = switchToRegister
    document.getElementById("user-register-button").onclick = userRegister
    document.getElementById("switch-to-login-button").onclick = switchToLogin
    document.getElementById("register-password").oninput = () => { passwordValidator(document.getElementById("register-password"), false); }
    document.getElementById("register-password-confirm").oninput = () => { passwordValidator(document.getElementById("register-password-confirm"), true); }
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

        if (!response.ok) {
            spinner.error()
            throw new Error("Not OK");
        }

        // Local storage
        localStorage.setItem("lastHost", host);
        localStorage.setItem("lastUsername", loginData.username);

        const jwtToken = await response.text();
        setCookie('jwtToken', jwtToken, 1);
        spinner.success()
        document.location.href = `app.html`;
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

function lastHost() {
    if (localStorage.getItem("lastHost")) {
        document.getElementById("login-form").host.value = localStorage.getItem("lastHost");
        document.getElementById("register-form").host.value = localStorage.getItem("lastHost");
        getHostSettings();
    }
    else if (document.location.hostname !== "localhost") {
        document.getElementById("login-form").host.value = document.location.origin;
        document.getElementById("register-form").host.value = document.location.origin;
        getHostSettings();
    }
}

function switchToRegister() {
    document.getElementById('login-form').classList.add("hidden");
    document.getElementById('register-form').classList.remove("hidden");
}

function switchToLogin() {
    document.getElementById('login-form').classList.remove("hidden");
    document.getElementById('register-form').classList.add("hidden");
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

        Modal.toggle({
            icon: "success",
            title: i18n.translateOne('login.register.success', host),
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