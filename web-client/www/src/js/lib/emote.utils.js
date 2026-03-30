/**
 * Detects if a message contains only emotes (emojis and/or custom emotes).
 * @param {string} message - The message to check
 * @param {string[]} acceptedEmoteWords - List of accepted words for custom emotes (e.g., ['test', 'smile'])
 * @returns {boolean} True if the message contains only emotes, false otherwise
 */
export function containsOnlyEmotes(message, acceptedEmoteWords = []) {
    const trimmed = message.trim();

    if (trimmed.length === 0) return false;

    const emojiPattern = /(?![\d#*])[\p{Emoji}\p{Emoji_Presentation}\p{Emoji_Modifier}\p{Emoji_Component}]/gu;

    const customEmotePattern = acceptedEmoteWords.length > 0
        ? new RegExp(`:(?:${acceptedEmoteWords.join('|')}):`, 'g')
        : null;

    let remaining = trimmed.replaceAll(emojiPattern, '');

    if (customEmotePattern) {
        remaining = remaining.replace(customEmotePattern, '');
    }

    remaining = remaining.replaceAll(/\s+/g, '');

    return remaining.length === 0;
}

/**
 * Counts the number of emotes (emojis and/or custom emotes) in a message.
 * @param {string} message - The message to check
 * @param {string[]} acceptedEmoteWords - List of accepted words for custom emotes (e.g., ['test', 'smile'])
 * @returns {number} The number of emotes found in the message
 */
export function countEmotes(message, acceptedEmoteWords = []) {
    const trimmed = message.trim();

    if (trimmed.length === 0) return 0;

    const emojiPattern = /(?![\d#*])[\p{Emoji}\p{Emoji_Presentation}\p{Emoji_Modifier}\p{Emoji_Component}]/gu;

    const emojiMatches = trimmed.match(emojiPattern) ?? [];

    const customEmotePattern = acceptedEmoteWords.length > 0
            ? new RegExp(`:(?:${acceptedEmoteWords.join('|')}):`, 'g')
            : null;

    const customEmoteMatches = customEmotePattern
            ? (trimmed.match(customEmotePattern) ?? [])
            : [];

    return emojiMatches.length + customEmoteMatches.length;
}