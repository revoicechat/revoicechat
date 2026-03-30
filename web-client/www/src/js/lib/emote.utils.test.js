import { describe, expect, test } from "vitest";
import { containsOnlyEmotes, countEmotes } from "./emote.utils.js";

describe('containsOnlyEmotes', () => {
    const acceptedWords = ['test', 'smile', 'heart', 'fire', "123"];

    test('only UTF-8 emojis', () => {
        expect(containsOnlyEmotes('😁✅🥜', acceptedWords)).toBe(true);
    });

    test('no custom emote', () => {
        expect(containsOnlyEmotes(':test: :smile:', [])).toBe(false);
    });

    test('only custom emote', () => {
        expect(containsOnlyEmotes(':test: :smile:', acceptedWords)).toBe(true);
    });

    test('UTF-8 emojis and custom emote', () => {
        expect(containsOnlyEmotes('😁 :test: ✅', acceptedWords)).toBe(true);
    });

    test('whitespace is trimmed', () => {
        expect(containsOnlyEmotes('   😁   ', acceptedWords)).toBe(true);
    });

    test('empty', () => {
        expect(containsOnlyEmotes('', acceptedWords)).toBe(false);
    });

    test('sentence with emoji', () => {
        expect(containsOnlyEmotes('Hello 😁', acceptedWords)).toBe(false);
    });

    test('sentence with custom emote', () => {
        expect(containsOnlyEmotes(':test: hello', acceptedWords)).toBe(false);
    });

    test('invalid custom emote', () => {
        expect(containsOnlyEmotes(':invalid:', acceptedWords)).toBe(false);
    });

    test('only numbers', () => {
        expect(containsOnlyEmotes('123', acceptedWords)).toBe(false);
    });

  test('custom emote only numbers', () => {
    expect(containsOnlyEmotes(':123:', acceptedWords)).toBe(true);
  });
})

describe('countEmotes', () => {
    const acceptedWords = ['test', 'smile', 'heart', 'fire', "123"];

    test('only UTF-8 emojis', () => {
        expect(countEmotes('😁✅🥜', acceptedWords)).toBe(3);
    });

    test('no custom emote', () => {
        expect(countEmotes(':test: :smile:', [])).toBe(0);
    });

    test('only custom emote', () => {
        expect(countEmotes(':test: :smile:', acceptedWords)).toBe(2);
    });

    test('UTF-8 emojis and custom emote', () => {
        expect(countEmotes('😁 :test: ✅', acceptedWords)).toBe(3);
    });

    test('whitespace is trimmed', () => {
        expect(countEmotes('   😁   ', acceptedWords)).toBe(1);
    });

    test('empty', () => {
        expect(countEmotes('', acceptedWords)).toBe(0);
    });

    test('sentence with emoji', () => {
        expect(countEmotes('Hello 😁', acceptedWords)).toBe(1);
    });

    test('sentence with custom emote', () => {
        expect(countEmotes(':test: hello', acceptedWords)).toBe(1);
    });

    test('invalid custom emote', () => {
        expect(countEmotes(':invalid:', acceptedWords)).toBe(0);
    });

    test('only numbers', () => {
        expect(countEmotes('123', acceptedWords)).toBe(0);
    });

    test('custom emote only numbers', () => {
        expect(countEmotes(':123:', acceptedWords)).toBe(1);
    });
})
