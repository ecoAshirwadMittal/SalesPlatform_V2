/**
 * Single source of truth for the backend API base path. Previously
 * redeclared in ~17 pages with inconsistent suffixes (`/api/v1`,
 * `/api/v1/pws`, `/api/v1/admin`). Call sites should import `API_BASE`
 * and append the route suffix inline so the path is visible locally.
 */
export const API_BASE = '/api/v1';
