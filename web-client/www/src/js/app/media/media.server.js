import { apiFetch } from "../../lib/tools.js";

export default class MediaServer {
    /** @type {MediaServer} */
    static #instance
    static #token

    /**
     * @param {URL} core
     * @param {string} token
     */
    static init(core, token) {
        MediaServer.#instance = new MediaServer(core);
        MediaServer.#token = token;
    }

    /** @param {URL} core */
    constructor(core) {
        this.url = `${core.protocol}//${core.host}/media`;
    }

    /**
     * @param {string} id
     * @param {string} t
     * @return {string}
     */
    static profiles(id, t = '') {
        return `${MediaServer.#instance.url}/profiles/${id}?t=${t}`
    }

    /**
     * @param {string} id
     * @param {string} t
     * @return {string}
     */
    static serverProfiles(id, t = '') {
        return `${MediaServer.#instance.url}/profiles/server/${id}?t=${t}`
    }

    /**
     * @param {string} id
     * @param {string} t
     * @return {string}
     */
    static emote(id, t = '') {
        return `${MediaServer.#instance.url}/emote/${id}?t=${t}`
    }

    static emoteAssets() {
        return `${MediaServer.#instance.url}/emote/`
    }

    /**
     * @param {string} id
     * @param {string} t
     * @return {string}
     */
    static attachments(id, t = '') {
        return `${MediaServer.#instance.url}/attachments/${id}?t=${t}`
    }

    /**
     * @param {string} id
     * @param {string} t
     * @return {string}
     */
    static attachmentsThumbnail(id, t = '') {
        return `${MediaServer.#instance.url}/attachments/thumbnail/${id}?t=${t}`
    }

    /**
     * @param {string} path
     * @param {HTTPMethod} method
     * @param {*} rawData
     * @param {boolean} timeout
     * @return {Promise<null|any|boolean>}
     */
    static async fetch(path, method = 'GET', rawData = null, timeout = true) {
        if (method === null) {
            method = 'GET';
        }

        let signal = null;

        if (timeout) {
            signal = AbortSignal.timeout(5000);
        }

        try {
            const response = await apiFetch(MediaServer.#instance.url + path, {
                method: method,
                signal: signal,
                headers: {
                    'Authorization': `Bearer ${MediaServer.#token}`
                },
                body: rawData
            });

            if (method !== "DELETE") {
                const contentType = response.headers.get("content-type");

                if (contentType?.includes("application/json")) {
                    return await response.json();
                }
            }

            return response.ok;
        }
        catch (error) {
            console.error(`fetchMedia: An error occurred while processing request \n${error}\nHost: ${(MediaServer.#instance.url)}\nPath: ${path}\nMethod: ${method}`);
            return null;
        }
    }
}