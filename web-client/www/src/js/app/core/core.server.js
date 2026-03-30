/**
 * @typedef {"GET"|"POST"|"PATCH"|"PUT"|"DELETE"|"OPTION"} HTTPMethod
 */
import {apiFetch} from "../../lib/tools.js";
import {Sse} from "./sse.js";

export default class CoreServer {

    /** @type {CoreServer} */
    static instance
    static #token

    /**
     * @param {URL} core
     * @param {string} token
     */
    static init(core, token) {
        CoreServer.instance = new CoreServer(`${core.protocol}//${core.host}`);
        CoreServer.#token = token;
    }

    /** @param {string} coreURL */
    constructor(coreURL) {
        this.url = coreURL;
    }

    /**
     * @param {string} path
     * @param {HTTPMethod} method
     * @param {*} data
     * @return {Promise<null|any|boolean>}
     */
    static async fetch(path, method = "GET", data = null) {
        if (method === null) {
            method = 'GET';
        }
        if (data) {
            data = JSON.stringify(data);
        }
        try {
            const response = await apiFetch(`${CoreServer.instance.url}/api${path}`, {
                method: method,
                signal: AbortSignal.timeout(5000),
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${CoreServer.#token}`
                },
                body: data
            });

            if (method !== "DELETE") {
                const contentType = response.headers.get("content-type");

                if (contentType?.includes("application/json")) {
                    return await response.json();
                }
                if (response.status === 204) {
                    return null
                }
            }

            return response.ok;
        } catch (error) {
            console.error(`fetchCore: An error occurred while processing request \n${error}\nHost: ${this.url}\nPath: ${path}\nMethod: ${method}`);
            return null;
        }
    }

    static voiceUrl() {
        return `${CoreServer.instance.url}/api/voice`;
    }

    static streamUrl() {
        return `${CoreServer.instance.url}/api/stream`;
    }

    /**
     * @param {(data) => void} handleSSEMessage
     * @param {() => void} handleSSEError
     */
    static sse(handleSSEMessage, handleSSEError) {
        return new Sse(
            CoreServer.#token,
            CoreServer.instance.url,
            handleSSEMessage,
            handleSSEError
        )
    }
}
