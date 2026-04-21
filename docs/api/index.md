# API Overview

Base URL (local): `http://localhost:8080`
All REST endpoints live under `/api/v1/**`.

## Interactive API Reference (Swagger UI)

Springdoc-openapi publishes a browsable UI and machine-readable spec on every
running backend:

| Surface | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui/index.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |
| OpenAPI YAML | http://localhost:8080/v3/api-docs.yaml |

Both paths are `permitAll` in `SecurityConfig` so you can reach the UI without
a login cookie — but every operation still requires auth to execute.

## Authenticating from Swagger UI

Two schemes are registered. Pick whichever is easier for your workflow:

### Option A — cookie (matches browser flow)

1. Hit `POST /api/v1/auth/login` from Swagger or via `curl` with a body like
   `{"email":"admin@test.com","password":"Admin123!","rememberMe":false}`.
2. The response sets an `HttpOnly; SameSite=Strict; auth_token=...` cookie in
   your browser. Swagger UI served from the same origin automatically attaches
   it on subsequent calls.
3. Because the cookie is `HttpOnly`, you cannot paste it into Swagger's
   "Authorize" dialog. Use Option B for that.

### Option B — bearer token (for copy-paste into Authorize)

1. Call `POST /api/v1/auth/login` from `curl` with the same payload.
2. Pull the JWT out of the `Set-Cookie: auth_token=...` response header.
3. Click **Authorize** in Swagger UI, choose `bearerAuth`, paste the raw JWT.
4. Swagger now sends `Authorization: Bearer <token>` on every request.

`JwtAuthenticationFilter` accepts either source (cookie first, then header)
per the 2026-04-13 auth-cookie ADR.

## Environments

The spec ships with two `servers[]` entries so the "Try it out" dropdown can
target local or QA:

| Environment | Base URL |
|---|---|
| Local dev | `http://localhost:8080` |
| QA | `https://buy-qa.ecoatmdirect.com` |

Prod is intentionally omitted — Swagger UI should stay off the public prod
surface. If/when prod access is required, expose it behind the VPN and add a
profile-gated `server` entry.

## Detailed endpoint reference

See [rest-endpoints.md](rest-endpoints.md) for prose-level documentation of
request/response shapes, filter semantics, and role requirements. The
OpenAPI spec is the single source of truth for field-level types; the
markdown doc is the single source of truth for business rules that don't
fit cleanly into an annotation.
