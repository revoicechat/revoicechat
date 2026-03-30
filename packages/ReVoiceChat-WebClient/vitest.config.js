import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    environment: 'jsdom',
    coverage: {
      provider: 'v8',
      reporter: ['lcov', 'text', 'html'],
      reportsDirectory: './coverage',
      exclude: [
        '**/*.test.js',
        '**/node_modules/**',
        '**/vendor/**',
      ]
    },
    include: ['**/*.test.js'],
  }
});