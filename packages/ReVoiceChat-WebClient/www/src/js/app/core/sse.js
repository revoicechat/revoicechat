export class Sse {
    #token;
    coreUrl;

    #handleSSEMessage;
    #handleSSEError;
    #sseAbortController

    /**
     *
     * @param {string} token
     * @param {string} coreUrl
     * @param {(data) => void} handleSSEMessage
     * @param {() => void} handleSSEError
     */
    constructor(token, coreUrl, handleSSEMessage, handleSSEError) {
        this.#token = token
        this.coreUrl = coreUrl
        this.#handleSSEMessage = handleSSEMessage
        this.#handleSSEError = handleSSEError
    }

    // Server Send Event avec JWT en header
    async openSSE() {
        this.closeSSE();
        this.#sseAbortController = new AbortController();
        try {
            const response = await fetch(`${this.coreUrl}/api/sse`, {
                method: 'GET',
                headers: {
                    'Authorization': `Bearer ${this.#token}`,
                    'Accept': 'text/event-stream',
                },
                signal: this.#sseAbortController.signal,
            })
            if (!response.ok) {
                throw new Error(`HTTP error! status: ${response.status}`);
            }

            const reader = response.body.getReader();
            const decoder = new TextDecoder();
            let buffer = '';

            const readStream = () => {
                reader.read().then(({ done, value }) => {
                    if (done) {
                        console.log('SSE stream closed');
                        this.#handleSSEError();
                        return;
                    }
                    buffer += decoder.decode(value, { stream: true });
                    const lines = buffer.split('\n');
                    buffer = lines.pop() || '';
                    let eventData = '';
                    for (const line of lines) {
                        if (line.startsWith('data:')) {
                            eventData = line.slice(5).trim();
                        } else if (line === '' && eventData) {
                            this.#handleSSEMessage(eventData);
                            eventData = '';
                        }
                    }
                    readStream();
                }).catch(error => {
                    if (error.name !== 'AbortError') {
                        console.error('SSE read error:', error);
                        this.#handleSSEError();
                    }
                });
            };
            readStream();
        } catch (error) {
            if (error.name !== 'AbortError') {
                console.error('SSE connection error:', error);
                this.#handleSSEError();
            }
        }
    }

    closeSSE() {
        if (this.#sseAbortController) {
            this.#sseAbortController.abort();
            this.#sseAbortController = null;
        }
    }
}