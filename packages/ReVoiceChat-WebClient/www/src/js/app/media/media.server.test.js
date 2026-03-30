import { describe, expect, test, beforeEach, vi } from "vitest";
import MediaServer from "./media.server.js";
import * as tools from "../../lib/tools.js";

// Mock the apiFetch function
vi.mock("../../lib/tools.js", () => ({
    apiFetch: vi.fn()
}));

describe('MediaServer', () => {

    beforeEach(() => {
        MediaServer.init(new URL("https://revoicechat.fr"), "1234");
        vi.clearAllMocks();
    });

    test('profiles', () => {
        const url = MediaServer.profiles('test', '15');
        expect(url).toBe("https://revoicechat.fr/media/profiles/test?t=15");
    });

    test('serverProfiles', () => {
        const url = MediaServer.serverProfiles('test', '15');
        expect(url).toBe("https://revoicechat.fr/media/profiles/server/test?t=15");
    });

    test('attachments', () => {
        const url = MediaServer.attachments('test', '15');
        expect(url).toBe("https://revoicechat.fr/media/attachments/test?t=15");
    });

    test('emote', () => {
        const url = MediaServer.emote('test', '15');
        expect(url).toBe("https://revoicechat.fr/media/emote/test?t=15");
    });

    describe('fetch', () => {
        test('should make GET request with correct URL and headers', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({ data: 'test' })
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await MediaServer.fetch('/test-path');

            expect(tools.apiFetch).toHaveBeenCalledWith(
                'https://revoicechat.fr/media/test-path',
                expect.objectContaining({
                    method: 'GET',
                    headers: {
                        'Authorization': 'Bearer 1234'
                    },
                    body: null
                })
            );
            expect(result).toEqual({ data: 'test' });
        });

        test('should make POST request with body', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({ success: true })
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const requestBody = { name: 'test' };
            const result = await MediaServer.fetch('/create', 'POST', requestBody);

            expect(tools.apiFetch).toHaveBeenCalledWith(
                'https://revoicechat.fr/media/create',
                expect.objectContaining({
                    method: 'POST',
                    body: requestBody
                })
            );
            expect(result).toEqual({ success: true });
        });

        test('should handle DELETE request and return ok status', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn()
                }
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await MediaServer.fetch('/item/123', 'DELETE');

            expect(tools.apiFetch).toHaveBeenCalledWith(
                'https://revoicechat.fr/media/item/123',
                expect.objectContaining({
                    method: 'DELETE'
                })
            );
            expect(result).toBe(true);
        });

        test('should handle DELETE request failure', async () => {
            const mockResponse = {
                ok: false,
                headers: {
                    get: vi.fn()
                }
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await MediaServer.fetch('/item/123', 'DELETE');

            expect(result).toBe(false);
        });

        test('should handle non-JSON response', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('text/plain')
                }
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await MediaServer.fetch('/text-endpoint', 'POST');

            expect(result).toBe(true);
        });

        test('should default to GET when method is null', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({})
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            await MediaServer.fetch('/test', null);

            expect(tools.apiFetch).toHaveBeenCalledWith(
                expect.any(String),
                expect.objectContaining({
                    method: 'GET'
                })
            );
        });

        test('should include timeout signal by default', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({})
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            await MediaServer.fetch('/test');

            const call = tools.apiFetch.mock.calls[0][1];
            expect(call.signal).toBeDefined();
        });

        test('should not include timeout signal when timeout is false', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({})
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            await MediaServer.fetch('/test', 'GET', null, false);

            const call = tools.apiFetch.mock.calls[0][1];
            expect(call.signal).toBeNull();
        });

        test('should handle fetch error and return null', async () => {
            const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

            tools.apiFetch.mockRejectedValue(new Error('Network error'));

            const result = await MediaServer.fetch('/error-path');

            expect(result).toBeNull();
            expect(consoleErrorSpy).toHaveBeenCalledWith(
                expect.stringContaining('fetchMedia: An error occurred while processing request')
            );

            consoleErrorSpy.mockRestore();
        });

        test('should handle timeout error', async () => {
            const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

            tools.apiFetch.mockRejectedValue(new DOMException('The operation was aborted', 'AbortError'));

            const result = await MediaServer.fetch('/timeout-path');

            expect(result).toBeNull();
            expect(consoleErrorSpy).toHaveBeenCalled();

            consoleErrorSpy.mockRestore();
        });

        test('should handle response with null content-type', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue(null)
                }
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await MediaServer.fetch('/no-content-type', 'PUT');

            expect(result).toBe(true);
        });

        test('should parse JSON for PUT request', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({ updated: true })
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await MediaServer.fetch('/update', 'PUT', { data: 'value' });

            expect(result).toEqual({ updated: true });
            expect(mockResponse.json).toHaveBeenCalled();
        });
    });
});