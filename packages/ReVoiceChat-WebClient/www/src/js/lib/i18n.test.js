import {afterEach, beforeEach, describe, expect, it, vi} from 'vitest';
import {I18n} from "./i18n.js";

describe('I18n', () => {
  let i18n;

  beforeEach(() => {
    i18n = new I18n('translations');
    document.body.innerHTML = '';
    vi.clearAllMocks();
  });

  afterEach(() => {
    i18n.destroy();
  });

  describe('parseProperties', () => {
    it('should parse basic key-value pairs with equals', () => {
      const content = 'key1=value1\nkey2=value2';
      const result = i18n.parseProperties(content);

      expect(result).toEqual({
        key1: 'value1',
        key2: 'value2'
      });
    });

    it('should parse key-value pairs with colon separator', () => {
      const content = 'key1:value1\nkey2:value2';
      const result = i18n.parseProperties(content);

      expect(result).toEqual({
        key1: 'value1',
        key2: 'value2'
      });
    });

    it('should ignore empty lines', () => {
      const content = 'key1=value1\n\n\nkey2=value2';
      const result = i18n.parseProperties(content);

      expect(result).toEqual({
        key1: 'value1',
        key2: 'value2'
      });
    });

    it('should ignore comments starting with #', () => {
      const content = '# This is a comment\nkey1=value1\n# Another comment\nkey2=value2';
      const result = i18n.parseProperties(content);

      expect(result).toEqual({
        key1: 'value1',
        key2: 'value2'
      });
    });

    it('should ignore comments starting with !', () => {
      const content = '! This is a comment\nkey1=value1\nkey2=value2';
      const result = i18n.parseProperties(content);

      expect(result).toEqual({
        key1: 'value1',
        key2: 'value2'
      });
    });

    it('should trim whitespace from keys and values', () => {
      const content = '  key1  =  value1  \n  key2  :  value2  ';
      const result = i18n.parseProperties(content);

      expect(result).toEqual({
        key1: 'value1',
        key2: 'value2'
      });
    });

    it('should handle lines without separator', () => {
      const content = 'key1=value1\ninvalidline\nkey2=value2';
      const result = i18n.parseProperties(content);

      expect(result).toEqual({
        key1: 'value1',
        key2: 'value2'
      });
    });

    it('should handle values with equals signs', () => {
      const content = 'url=https://example.com?param=value';
      const result = i18n.parseProperties(content);
      expect(result.url).toBe('https://example.com?param=value');
    });
  });

  describe('formatString', () => {
    it('should replace single placeholder with string value', () => {
      const result = i18n.formatString('Hello {0}', 'World');
      expect(result).toBe('Hello World');
    });

    it('should replace multiple placeholders with array values', () => {
      const result = i18n.formatString('Hello {0}, you are {1} years old', ['Alice', '25']);
      expect(result).toBe('Hello Alice, you are 25 years old');
    });

    it('should keep placeholder if value is undefined', () => {
      const result = i18n.formatString('Hello {0} and {1}', ['World']);
      expect(result).toBe('Hello World and {1}');
    });

    it('should handle non-sequential placeholders', () => {
      const result = i18n.formatString('Value {2} and {0}', ['first', 'second', 'third']);
      expect(result).toBe('Value third and first');
    });

    it('should return empty string for empty template', () => {
      const result = i18n.formatString('', ['value']);
      expect(result).toBe('');
    });

    it('should return empty string for null template', () => {
      const result = i18n.formatString(null, ['value']);
      expect(result).toBe('');
    });

    it('should handle template with no placeholders', () => {
      const result = i18n.formatString('No placeholders here', ['value']);
      expect(result).toBe('No placeholders here');
    });
  });

  describe('loadTranslations', () => {
    beforeEach(() => {
      globalThis.fetch = vi.fn();
    });

    it('should load translations for specified language', async () => {
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'data'
      });
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'hello=Hello\nbye=Goodbye\nmessage=this is a message'
      });
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'hello=Bonjour\nbye=Au revoir'
      });

      const result = await i18n.loadTranslations('fr');

      expect(globalThis.fetch).toHaveBeenCalledWith('translations/data_fr.properties');
      expect(result).toEqual({
        hello: 'Bonjour',
        bye: 'Au revoir',
        message: 'this is a message'
      });
      expect(i18n.translationsLoaded).toBe(true);
    });

    it('should throw error when response is not ok', async () => {
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'data'
      });
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'hello=Hello\nbye=Goodbye'
      });
      globalThis.fetch.mockResolvedValueOnce({
        ok: false,
        status: 404
      });

      await expect(i18n.loadTranslations('fr')).rejects.toThrow('Cannot read properties of undefined (reading \'text\')');
      expect(i18n.translationsLoaded).toBe(true);
    });

    it('should fallback to English if language file not found', async () => {
      const consoleWarnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
      const mockEnglishContent = 'hello=Hello\nbye=Goodbye';
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'data'
      });
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'hello=Hello\nbye=Goodbye'
      });
      globalThis.fetch
          .mockResolvedValueOnce({
            ok: false,
            status: 404
          })
          .mockResolvedValueOnce({
            ok: true,
            text: async () => mockEnglishContent
          });

      const result = await i18n.loadTranslations('xx');

      expect(consoleWarnSpy).toHaveBeenCalledWith('Language xx not found, falling back to English');
      expect(result).toEqual({
        hello: 'Hello',
        bye: 'Goodbye'
      });
      expect(i18n.translationsLoaded).toBe(true);

      consoleWarnSpy.mockRestore();
    });

    it('should throw error if English fallback also fails', async () => {
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'data'
      });
      globalThis.fetch.mockRejectedValue(new Error('Network error'));

      await expect(i18n.loadTranslations('en')).rejects.toThrow('Network error');
      expect(i18n.translationsLoaded).toBe(true);
    });

    it('should set translationsLoaded to true even on error', async () => {
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'data'
      });
      globalThis.fetch.mockRejectedValue(new Error('Network error'));

      try {
        await i18n.loadTranslations('en');
      } catch (e) {
        console.log(`${e} Expected`);
      }

      expect(i18n.translationsLoaded).toBe(true);
    });
  });

  describe('translateElement', () => {
    beforeEach(() => {
      i18n.translations = {
        'greeting': 'Hello',
        'volume': 'Volume {0}%',
        'user.info': '{0} is {1} years old'
      };
    });

    it('should translate element without values', () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'greeting';

      i18n.translateElement(element);

      expect(element.textContent).toBe('Hello');
    });

    it('should translate element with single value', () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'volume';
      element.dataset.i18nValue = '75';

      i18n.translateElement(element);

      expect(element.textContent).toBe('Volume 75%');
    });

    it('should translate element with array values', () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'user.info';
      element.dataset.i18nValue = '["Alice", "30"]';

      i18n.translateElement(element);

      expect(element.textContent).toBe('Alice is 30 years old');
    });

    it('should warn about missing translation key', () => {
      const consoleWarnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
      const element = document.createElement('div');
      element.dataset.i18n = 'nonexistent';

      i18n.translateElement(element);

      expect(consoleWarnSpy).toHaveBeenCalledWith('Missing translation for key: nonexistent');
      consoleWarnSpy.mockRestore();
    });

    it('should handle invalid JSON in i18n-value', () => {
      const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
      const element = document.createElement('div');
      element.dataset.i18n = 'volume';
      element.dataset.i18nValue = '[invalid json]';

      i18n.translateElement(element);

      expect(element.textContent).toBe('Volume {0}%');
      expect(consoleErrorSpy).toHaveBeenCalled();
      consoleErrorSpy.mockRestore();
    });
  });

  describe('translatePage', () => {
    beforeEach(() => {
      i18n.translationsLoaded = true;
      i18n.translations = {
        'title': 'Welcome',
        'tooltip': 'Click me',
        'search': 'Search...',
        'volume': 'Volume {0}%'
      };
    });

    it('should not translate if translations not loaded', () => {
      i18n.translationsLoaded = false;
      const element = document.createElement('div');
      element.dataset.i18n = 'title';
      document.body.appendChild(element);

      i18n.translatePage();

      expect(element.textContent).toBe('');
    });

    it('should translate all elements with data-i18n', () => {
      const element1 = document.createElement('div');
      element1.dataset.i18n = 'title';
      const element2 = document.createElement('span');
      element2.dataset.i18n = 'title';
      document.body.appendChild(element1);
      document.body.appendChild(element2);

      i18n.translatePage();

      expect(element1.textContent).toBe('Welcome');
      expect(element2.textContent).toBe('Welcome');
    });

    it('should setup observer for elements with data-i18n-value', () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'volume';
      element.dataset.i18nValue = '50';
      document.body.appendChild(element);

      i18n.translatePage();

      expect(i18n.observers.has(element)).toBe(true);
      expect(element.textContent).toBe('Volume 50%');
    });

    it('should translate title attributes', () => {
      const element = document.createElement('button');
      element.dataset.i18nTitle = 'tooltip';
      document.body.appendChild(element);

      i18n.translatePage();

      expect(element.getAttribute('title')).toBe('Click me');
    });

    it('should translate placeholder attributes', () => {
      const element = document.createElement('input');
      element.dataset.i18nPlaceholder = 'search';
      document.body.appendChild(element);

      i18n.translatePage();

      expect(element.getAttribute('placeholder')).toBe('Search...');
    });

    it('should warn about missing title translation', () => {
      const consoleWarnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
      const element = document.createElement('button');
      element.dataset.i18nTitle = 'nonexistent';
      document.body.appendChild(element);

      i18n.translatePage();

      expect(consoleWarnSpy).toHaveBeenCalledWith('Missing translation for title key: nonexistent');
      consoleWarnSpy.mockRestore();
    });

    it('should warn about missing placeholder translation', () => {
      const consoleWarnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});
      const element = document.createElement('input');
      element.dataset.i18nPlaceholder = 'nonexistent';
      document.body.appendChild(element);

      i18n.translatePage();

      expect(consoleWarnSpy).toHaveBeenCalledWith('Missing translation for placeholder key: nonexistent');
      consoleWarnSpy.mockRestore();
    });

    it('should search inside shadow DOM', () => {
      // Create a host element with shadow DOM
      const host = document.createElement('div');
      const shadowRoot = host.attachShadow({ mode: 'open' });

      const shadowElement = document.createElement('span');
      shadowElement.dataset.i18n = 'title';
      shadowRoot.appendChild(shadowElement);

      document.body.appendChild(host);

      i18n.translatePage();

      expect(shadowElement.textContent).toBe('Welcome');
    });
  });

  describe('translateOne', () => {
    beforeEach(() => {
      i18n.translationsLoaded = true;
      i18n.translations = {
        'hello': 'Hello',
        'greeting': 'Hello {0}'
      };
    });

    it('should return key if translations not loaded', () => {
      i18n.translationsLoaded = false;
      const result = i18n.translateOne('hello');
      expect(result).toBe('hello');
    });

    it('should return translation for key', () => {
      const result = i18n.translateOne('hello');
      expect(result).toBe('Hello');
    });

    it('should return key if translation not found', () => {
      const result = i18n.translateOne('nonexistent');
      expect(result).toBe('nonexistent');
    });

    it('should format translation with values', () => {
      const result = i18n.translateOne('greeting', 'World');
      expect(result).toBe('Hello World');
    });

    it('should format translation with array values', () => {
      i18n.translations['user.info'] = '{0} is {1}';
      const result = i18n.translateOne('user.info', ['Alice', '30']);
      expect(result).toBe('Alice is 30');
    });
  });

  describe('updateValue', () => {
    it('should update element value by ID', () => {
      const element = document.createElement('div');
      element.id = 'test-element';
      element.dataset.i18n = 'key';
      document.body.appendChild(element);

      i18n.updateValue('test-element', 'new value');

      expect(element.dataset.i18nValue).toBe('new value');
    });

    it('should update element value by reference', () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'key';

      i18n.updateValue(element, 'new value');

      expect(element.dataset.i18nValue).toBe('new value');
    });

    it('should stringify array values', () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'key';

      i18n.updateValue(element, ['value1', 'value2']);

      expect(element.dataset.i18nValue).toBe('["value1","value2"]');
    });

    it('should warn if element not found by ID', () => {
      const consoleWarnSpy = vi.spyOn(console, 'warn').mockImplementation(() => {});

      i18n.updateValue('nonexistent', 'value');

      expect(consoleWarnSpy).toHaveBeenCalledWith('Element not found: nonexistent');
      consoleWarnSpy.mockRestore();
    });
  });

  describe('observeElement', () => {
    beforeEach(() => {
      i18n.translationsLoaded = true;
      i18n.translations = {
        'volume': 'Volume {0}%'
      };
    });

    it('should setup MutationObserver for element', () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'volume';
      element.dataset.i18nValue = '50';

      i18n.observeElement(element);

      expect(i18n.observers.has(element)).toBe(true);
    });

    it('should disconnect existing observer before creating new one', () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'volume';

      i18n.observeElement(element);
      const firstObserver = i18n.observers.get(element);
      const disconnectSpy = vi.spyOn(firstObserver, 'disconnect');

      i18n.observeElement(element);

      expect(disconnectSpy).toHaveBeenCalled();
    });

    it('should re-translate element when data-i18n-value changes', async () => {
      const element = document.createElement('div');
      element.dataset.i18n = 'volume';
      element.dataset.i18nValue = '50';
      document.body.appendChild(element);

      i18n.observeElement(element);
      i18n.translateElement(element);
      expect(element.textContent).toBe('Volume 50%');

      element.dataset.i18nValue = '75';

      await vi.waitFor(() => {
        expect(element.textContent).toBe('Volume 75%');
      });
    });
  });

  describe('destroy', () => {
    it('should disconnect all observers', () => {
      const element1 = document.createElement('div');
      const element2 = document.createElement('div');

      i18n.observeElement(element1);
      i18n.observeElement(element2);

      const observer1 = i18n.observers.get(element1);
      const observer2 = i18n.observers.get(element2);
      const disconnectSpy1 = vi.spyOn(observer1, 'disconnect');
      const disconnectSpy2 = vi.spyOn(observer2, 'disconnect');

      i18n.destroy();

      expect(disconnectSpy1).toHaveBeenCalled();
      expect(disconnectSpy2).toHaveBeenCalled();
      expect(i18n.observers.size).toBe(0);
    });
  });

  describe('translate', () => {
    beforeEach(() => {
      globalThis.fetch = vi.fn();
    });

    it('should load translations and translate page', async () => {
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'data'
      });
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'hello=Hello'
      });
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'hello=Bonjour'
      });

      const element = document.createElement('div');
      element.dataset.i18n = 'hello';
      document.body.appendChild(element);

      await i18n.translate('fr');

      expect(i18n.translations).toEqual({ hello: 'Bonjour' });
      expect(element.textContent).toBe('Bonjour');
    });

    it('should log success message', async () => {
      const consoleLogSpy = vi.spyOn(console, 'log').mockImplementation(() => {});
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'data'
      });
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'hello=Hello'
      });

      await i18n.translate('en');

      expect(consoleLogSpy).toHaveBeenCalledWith('Language changed to: en');
      consoleLogSpy.mockRestore();
    });

    it('should log error on failure', async () => {
      const consoleErrorSpy = vi.spyOn(console, 'error').mockImplementation(() => {});
      globalThis.fetch.mockResolvedValueOnce({
        ok: true,
        text: async () => 'data'
      });
      globalThis.fetch.mockRejectedValueOnce(new Error('Network error'));

      await i18n.translate('en');

      expect(consoleErrorSpy).toHaveBeenCalledWith('Error loading translations:', expect.any(Error));
      consoleErrorSpy.mockRestore();
    });
  });
});