import { describe, expect, test, beforeEach, vi } from "vitest";
import CoreServer from "./core.server.js";
import { Sse } from "./sse.js";
import * as tools from "../../lib/tools.js";

// Mock the apiFetch function
vi.mock("../../lib/tools.js", () => ({
    apiFetch: vi.fn()
}));

describe('CoreServer', () => {

    beforeEach(() => {
        CoreServer.init(new URL("https://revoicechat.fr"), "test-token-1234");
        vi.clearAllMocks();
    });

    describe('init', () => {
        test('should initialize instance with correct URL', () => {
            expect(CoreServer.instance).toBeDefined();
            expect(CoreServer.instance.url).toBe("https://revoicechat.fr");
        });
    });

    describe('voiceUrl', () => {
        test('should return correct voice URL', () => {
            const url = CoreServer.voiceUrl();
            expect(url).toBe("https://revoicechat.fr/api/voice");
        });
    });

    describe('streamUrl', () => {
        test('should return correct stream URL', () => {
            const url = CoreServer.streamUrl();
            expect(url).toBe("https://revoicechat.fr/api/stream");
        });
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

            const result = await CoreServer.fetch('/users');

            expect(tools.apiFetch).toHaveBeenCalledWith(
                'https://revoicechat.fr/api/users',
                expect.objectContaining({
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                        'Authorization': 'Bearer test-token-1234'
                    },
                    body: null
                })
            );
            expect(result).toEqual({ data: 'test' });
        });

        test('should make POST request with JSON body', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({ id: 123 })
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const requestData = { name: 'John', email: 'john@example.com' };
            const result = await CoreServer.fetch('/users', 'POST', requestData);

            expect(tools.apiFetch).toHaveBeenCalledWith(
                'https://revoicechat.fr/api/users',
                expect.objectContaining({
                    method: 'POST',
                    body: JSON.stringify(requestData)
                })
            );
            expect(result).toEqual({ id: 123 });
        });

        test('should make PATCH request with JSON body', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({ updated: true })
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const updateData = { name: 'Jane' };
            const result = await CoreServer.fetch('/users/123', 'PATCH', updateData);

            expect(tools.apiFetch).toHaveBeenCalledWith(
                'https://revoicechat.fr/api/users/123',
                expect.objectContaining({
                    method: 'PATCH',
                    body: JSON.stringify(updateData)
                })
            );
            expect(result).toEqual({ updated: true });
        });

        test('should make PUT request with JSON body', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({ replaced: true })
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const putData = { name: 'Complete Replace' };
            const result = await CoreServer.fetch('/users/123', 'PUT', putData);

            expect(result).toEqual({ replaced: true });
        });

        test('should handle DELETE request and return ok status', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn()
                }
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await CoreServer.fetch('/users/123', 'DELETE');

            expect(tools.apiFetch).toHaveBeenCalledWith(
                'https://revoicechat.fr/api/users/123',
                expect.objectContaining({
                    method: 'DELETE',
                    body: null
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

            const result = await CoreServer.fetch('/users/123', 'DELETE');
            expect(result).toBe(false);
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

            await CoreServer.fetch('/test', null);

            expect(tools.apiFetch).toHaveBeenCalledWith(
                expect.any(String),
                expect.objectContaining({
                    method: 'GET'
                })
            );
        });

        test('should include timeout signal', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({})
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            await CoreServer.fetch('/test');

            const call = tools.apiFetch.mock.calls[0][1];
            expect(call.signal).toBeDefined();
        });

        test('should handle non-JSON response', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('text/plain')
                }
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await CoreServer.fetch('/plain-text', 'POST', { data: 'test' });

            expect(result).toBe(true);
        });

        test('should handle fetch error and return null', async () => {
            const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

            tools.apiFetch.mockRejectedValue(new Error('Network error'));

            const result = await CoreServer.fetch('/error-path');

            expect(result).toBeNull();
            expect(consoleErrorSpy).toHaveBeenCalledWith(
                expect.stringContaining('fetchCore: An error occurred while processing request')
            );

            consoleErrorSpy.mockRestore();
        });

        test('should handle timeout error', async () => {
            const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});

            tools.apiFetch.mockRejectedValue(new DOMException('The operation was aborted', 'AbortError'));

            const result = await CoreServer.fetch('/timeout-path');

            expect(result).toBeNull();

            consoleErrorSpy.mockRestore();
        });

        test('should not stringify null data', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue('application/json')
                },
                json: vi.fn().mockResolvedValue({})
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            await CoreServer.fetch('/test', 'GET', null);

            const call = tools.apiFetch.mock.calls[0][1];
            expect(call.body).toBeNull();
        });

        test('should handle response with null content-type', async () => {
            const mockResponse = {
                ok: true,
                headers: {
                    get: vi.fn().mockReturnValue(null)
                }
            };

            tools.apiFetch.mockResolvedValue(mockResponse);

            const result = await CoreServer.fetch('/no-content-type', 'POST', { data: 'test' });

            expect(result).toBe(true);
        });
    });

    describe('sse', () => {
        test('should create SSE instance with correct parameters', () => {
            const handleMessage = vi.fn();
            const handleError = vi.fn();

            const sseInstance = CoreServer.sse(handleMessage, handleError);

            expect(sseInstance).toBeInstanceOf(Sse);
            expect(sseInstance.coreUrl).toBe("https://revoicechat.fr");
        });
    });
});