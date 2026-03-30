import { beforeEach, afterEach, describe, expect, test } from 'vitest';

import './icon.component.js';
import {icons} from "./icon.component.js";

describe('IconComponent', () => {
    for (let iconsKey in icons) {
        describe(iconsKey, () => {
            beforeEach(async () => {
                const icon = document.createElement(iconsKey);
                document.body.appendChild(icon);
                await new Promise(resolve => setTimeout(resolve, 0));
            });

            afterEach(() => {
                document.body.innerHTML = '';
            });
            test('should render an SVG element', () => {
                const svg = document.querySelectorAll('svg');
                expect(svg).not.toBeNull();
                expect(svg.length).toBe(1);
            });
        });
    }
});