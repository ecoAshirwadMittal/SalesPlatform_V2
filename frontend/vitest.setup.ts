import '@testing-library/jest-dom/vitest';
import { afterEach } from 'vitest';
import { cleanup } from '@testing-library/react';

// React Testing Library auto-cleanup runs when vitest `globals: true`.
// This project keeps globals off, so wire cleanup manually to avoid DOM
// leakage between tests within the same file.
afterEach(() => {
  cleanup();
});
