import {beforeEach, afterEach, describe, expect, test} from 'vitest';

import './notification.dot.component.js';

describe('DotComponent', () => {
    let icon;
    let shadowRoot;
    beforeEach(async () => {
        icon = document.createElement('revoice-notification-dot');
        icon.setAttribute('type', 'status');
        document.body.appendChild(icon);
        await new Promise(resolve => setTimeout(resolve, 0));
        shadowRoot = icon.shadowRoot;
    });
    afterEach(() => {
        document.body.innerHTML = '';
    });

    test('should render an element', () => {
        const data = document.querySelector('revoice-notification-dot');
        expect(data).not.toBeNull();
    });

    test('should have shadow root', () => {
        expect(shadowRoot).not.toBeNull();
    });

    test('should render content in shadow DOM', () => {
        const element = shadowRoot.querySelector('.dot');
        expect(element).not.toBeNull();
    });
});