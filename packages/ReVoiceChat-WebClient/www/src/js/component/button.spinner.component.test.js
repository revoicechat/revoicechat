import { beforeEach, afterEach, describe, expect, test, vi } from 'vitest';
import { SpinnerOnButton } from './button.spinner.component.js';

describe('SpinnerOnButton', () => {
  let container;
  let button;
  let spinner;

  beforeEach(() => {
    // Setup DOM
    container = document.createElement('div');
    button = document.createElement('button');
    button.id = 'test-button';
    button.textContent = 'Submit';
    container.appendChild(button);
    document.body.appendChild(container);
  });

  afterEach(() => {
    // Cleanup DOM
    container.remove();
    vi.clearAllTimers();
  });

  describe('Constructor', () => {
    test('should initialize with valid button id', () => {
      spinner = new SpinnerOnButton('test-button');

      expect(spinner.button).toBe(button);
      expect(spinner.spinnerEl).toBeNull();
      expect(spinner.checkmarkEl).toBeNull();
      expect(spinner.crossEl).toBeNull();
      expect(spinner.originalDisabledState).toBe(false);
    });

    test('should store original disabled state', () => {
      button.disabled = true;
      spinner = new SpinnerOnButton('test-button');

      expect(spinner.originalDisabledState).toBe(true);
    });

    test('should throw error when button not found', () => {
      expect(() => new SpinnerOnButton('non-existent-button')).toThrow('Button with id "non-existent-button" not found');
    });
  });

  describe('run()', () => {
    beforeEach(() => {
      spinner = new SpinnerOnButton('test-button');
    });

    test('should disable button', () => {
      spinner.run();

      expect(button.disabled).toBe(true);
    });

    test('should create and show spinner element', () => {
      spinner.run();

      expect(spinner.spinnerEl).not.toBeNull();
      expect(spinner.spinnerEl.className).toContain('btn-icon spinner');
      expect(spinner.spinnerEl.classList.contains('active')).toBe(true);
      expect(button.contains(spinner.spinnerEl)).toBe(true);
    });

    test('should reuse existing spinner element', () => {
      spinner.run();
      const firstSpinner = spinner.spinnerEl;

      spinner.run();

      expect(spinner.spinnerEl).toBe(firstSpinner);
    });

    test('should hide checkmark when showing spinner', () => {
      spinner.run();
      spinner.success(0);

      spinner.run();

      expect(spinner.checkmarkEl.classList.contains('active')).toBe(false);
      expect(spinner.spinnerEl.classList.contains('active')).toBe(true);
    });

    test('should hide cross when showing spinner', () => {
      spinner.run();
      spinner.error(0);

      spinner.run();

      expect(spinner.crossEl.classList.contains('active')).toBe(false);
      expect(spinner.spinnerEl.classList.contains('active')).toBe(true);
    });
  });

  describe('success()', () => {
    beforeEach(() => {
      vi.useFakeTimers();
      spinner = new SpinnerOnButton('test-button');
      spinner.run();
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    test('should hide spinner', () => {
      spinner.success();

      expect(spinner.spinnerEl.classList.contains('active')).toBe(false);
    });

    test('should create and show checkmark', () => {
      spinner.success();

      expect(spinner.checkmarkEl).not.toBeNull();
      expect(spinner.checkmarkEl.classList.contains('active')).toBe(true);
      expect(button.contains(spinner.checkmarkEl)).toBe(true);
    });

    test('should create SVG checkmark with correct attributes', () => {
      spinner.success();

      const svg = spinner.checkmarkEl;
      expect(svg.tagName).toBe('svg');
      expect(svg.getAttribute('class')).toBe('btn-icon checkmark active');
      expect(svg.getAttribute('viewBox')).toBe('0 0 52 52');

      const circle = svg.querySelector('.checkmark-circle');
      expect(circle).not.toBeNull();
      expect(circle.getAttribute('cx')).toBe('26');
      expect(circle.getAttribute('cy')).toBe('26');
      expect(circle.getAttribute('r')).toBe('25');

      const path = svg.querySelector('.checkmark-check');
      expect(path).not.toBeNull();
      expect(path.getAttribute('d')).toBe('M14.1 27.2l7.1 7.2 16.7-16.8');
    });

    test('should reset after default delay', () => {
      spinner.success();

      vi.advanceTimersByTime(2000);

      expect(spinner.checkmarkEl.classList.contains('active')).toBe(false);
      expect(button.disabled).toBe(false);
    });

    test('should reset after custom delay', () => {
      spinner.success(3000);

      vi.advanceTimersByTime(2999);
      expect(spinner.checkmarkEl.classList.contains('active')).toBe(true);

      vi.advanceTimersByTime(1);
      expect(spinner.checkmarkEl.classList.contains('active')).toBe(false);
    });

    test('should reuse existing checkmark element', () => {
      spinner.success(0);
      const firstCheckmark = spinner.checkmarkEl;

      spinner.run();
      spinner.success(0);

      expect(spinner.checkmarkEl).toBe(firstCheckmark);
    });
  });

  describe('error()', () => {
    beforeEach(() => {
      vi.useFakeTimers();
      spinner = new SpinnerOnButton('test-button');
      spinner.run();
    });

    afterEach(() => {
      vi.useRealTimers();
    });

    test('should hide spinner', () => {
      spinner.error();

      expect(spinner.spinnerEl.classList.contains('active')).toBe(false);
    });

    test('should create and show cross', () => {
      spinner.error();

      expect(spinner.crossEl).not.toBeNull();
      expect(spinner.crossEl.classList.contains('active')).toBe(true);
      expect(button.contains(spinner.crossEl)).toBe(true);
    });

    test('should create SVG cross with correct attributes', () => {
      spinner.error();

      const svg = spinner.crossEl;
      expect(svg.tagName).toBe('svg');
      expect(svg.getAttribute('class')).toBe('btn-icon cross active');
      expect(svg.getAttribute('viewBox')).toBe('0 0 52 52');

      const lines = svg.querySelectorAll('.cross-line');
      expect(lines.length).toBe(2);

      expect(lines[0].getAttribute('x1')).toBe('16');
      expect(lines[0].getAttribute('y1')).toBe('16');
      expect(lines[0].getAttribute('x2')).toBe('36');
      expect(lines[0].getAttribute('y2')).toBe('36');

      expect(lines[1].getAttribute('x1')).toBe('36');
      expect(lines[1].getAttribute('y1')).toBe('16');
      expect(lines[1].getAttribute('x2')).toBe('16');
      expect(lines[1].getAttribute('y2')).toBe('36');
    });

    test('should reset after default delay', () => {
      spinner.error();

      vi.advanceTimersByTime(2000);

      expect(spinner.crossEl.classList.contains('active')).toBe(false);
      expect(button.disabled).toBe(false);
    });

    test('should reset after custom delay', () => {
      spinner.error(5000);

      vi.advanceTimersByTime(4999);
      expect(spinner.crossEl.classList.contains('active')).toBe(true);

      vi.advanceTimersByTime(1);
      expect(spinner.crossEl.classList.contains('active')).toBe(false);
    });

    test('should reuse existing cross element', () => {
      spinner.error(0);
      const firstCross = spinner.crossEl;

      spinner.run();
      spinner.error(0);

      expect(spinner.crossEl).toBe(firstCross);
    });
  });

  describe('reset()', () => {
    beforeEach(() => {
      spinner = new SpinnerOnButton('test-button');
    });

    test('should hide all icons', () => {
      spinner.run();
      spinner.success(0);
      spinner.error(0);

      spinner.reset();

      expect(spinner.spinnerEl.classList.contains('active')).toBe(false);
      expect(spinner.checkmarkEl.classList.contains('active')).toBe(false);
      expect(spinner.crossEl.classList.contains('active')).toBe(false);
    });

    test('should restore original disabled state', () => {
      spinner.run();
      expect(button.disabled).toBe(true);

      spinner.reset();

      expect(button.disabled).toBe(false);
    });

    test('should restore original disabled state when button was initially disabled', () => {
      button.disabled = true;
      spinner = new SpinnerOnButton('test-button');

      spinner.run();
      spinner.reset();

      expect(button.disabled).toBe(true);
    });

    test('should handle reset when spinner element does not exist', () => {
      expect(() => {
        spinner.reset();
      }).not.toThrow();
    });

    test('should handle reset when only spinner exists', () => {
      spinner.run();

      expect(() => {
        spinner.reset();
      }).not.toThrow();

      expect(spinner.spinnerEl.classList.contains('active')).toBe(false);
    });
  });

  describe('Edge cases and integration', () => {
    beforeEach(() => {
      spinner = new SpinnerOnButton('test-button');
    });

    test('should handle multiple run calls', () => {
      spinner.run();
      spinner.run();
      spinner.run();

      expect(spinner.spinnerEl.classList.contains('active')).toBe(true);
      expect(button.disabled).toBe(true);
    });

    test('should handle success without prior run', () => {
      expect(() => {
        spinner.success();
      }).not.toThrow();
    });

    test('should handle error without prior run', () => {
      expect(() => {
        spinner.error();
      }).not.toThrow();
    });

    test('should handle complete workflow: run -> success -> run -> error', () => {
      vi.useFakeTimers();

      spinner.run();
      expect(button.disabled).toBe(true);

      spinner.success(0);
      expect(spinner.checkmarkEl.classList.contains('active')).toBe(true);

      spinner.run();
      expect(spinner.spinnerEl.classList.contains('active')).toBe(true);

      spinner.error(0);
      expect(spinner.crossEl.classList.contains('active')).toBe(true);

      vi.useRealTimers();
    });
  });
});