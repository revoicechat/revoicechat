import {beforeEach, afterEach, describe, expect, test} from 'vitest';

// Import custom elements
import './status.dot.component.js';

describe('DotComponent', () => {
    let icon;
    let shadowRoot;
    beforeEach(async () => {
        icon = document.createElement('revoice-status-dot');
        document.body.appendChild(icon);
        await new Promise(resolve => setTimeout(resolve, 0));
        shadowRoot = icon.shadowRoot;
    });
    afterEach(() => {
        document.body.innerHTML = '';
    });

    test('should render an element', () => {
        const data = document.querySelector('revoice-status-dot');
        expect(data).not.toBeNull();
    });

    test('should have shadow root', () => {
        expect(shadowRoot).not.toBeNull();
    });

    test('should render content in shadow DOM', () => {
        const element = shadowRoot.querySelector('.background-gray');
        expect(element).not.toBeNull();
    });

    test('should have specific styles or structure', () => {
        const styles = shadowRoot.querySelector('.background-green');
        expect(styles).toBeNull();
    });

    test('change color', () => {
        let styles = shadowRoot.querySelector('.background-red');
        expect(styles).toBeNull();
        const data = document.querySelector('revoice-status-dot');
        data.setAttribute('color', 'red');
        styles = shadowRoot.querySelector('.background-red');
        expect(styles).not.toBeNull();
    });

    test('change to same color', () => {
        const data = document.querySelector('revoice-status-dot');
        data.setAttribute('color', 'red');
        let styles = shadowRoot.querySelector('.background-red');
        expect(styles).not.toBeNull();
        data.setAttribute('color', 'red');
        styles = shadowRoot.querySelector('.background-red');
        expect(styles).not.toBeNull();
    });

    test('change to unknown color', () => {
        const data = document.querySelector('revoice-status-dot');
        data.setAttribute('color', 'not a color');
        let styles = shadowRoot.querySelector('.background-red');
        expect(styles).toBeNull();
        styles = shadowRoot.querySelector('.background-gray');
        expect(styles).not.toBeNull();
    });
});