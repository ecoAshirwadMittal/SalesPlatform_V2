# User Authentication & Password Reset

## Authentication Overview

- External users (buyers, bidders) authenticate via `POST /api/v1/auth/login` with
  email + BCrypt password.
- The JWT is delivered as an `HttpOnly; SameSite=Strict` cookie (`auth_token`). It is
  never returned in the response body to prevent XSS token theft.
- Internal users (`@ecoatm.com` emails) are redirected to Azure AD SSO via
  `GET /api/v1/auth/sso`.
- Roles are encoded in the JWT and enforced at both the Spring Security filter chain
  (`requestMatchers`) and method security (`@PreAuthorize`).

## Password Reset Flow (Phase 14)

### High-Level Steps

```
1. User clicks "Forgot Password?" on /login  →  navigates to /forgot-password
2. User enters email  →  POST /api/v1/auth/forgot-password
3. Backend issues a short-lived token, logs the raw token at INFO (Phase 14 dev mode)
4. [Future: email is sent with a link to /reset-password?token=<raw-token>]
5. User opens the link  →  /reset-password?token=<token>
6. User enters new password + confirm  →  POST /api/v1/auth/reset-password
7. Backend validates token, updates BCrypt hash, marks token consumed
8. User is redirected to /login
```

### Enumeration Resistance

`POST /api/v1/auth/forgot-password` always returns `200 OK` regardless of whether
the submitted email maps to an active account. The frontend always shows the same
generic message. This prevents an attacker from discovering valid email addresses
by probing the endpoint.

### Token Lifecycle

| Property | Value |
|----------|-------|
| TTL | 30 minutes |
| Storage | `identity.password_reset_tokens` (V75) |
| Token format | 32 bytes SecureRandom → Base64URL (43 chars, no padding) |
| Stored hash | SHA-256 hex digest (64 chars) — raw token is never persisted |
| One-time use | `consumed_at` is set on redemption; subsequent calls with the same token return `400` |
| Purge on new request | All unconsumed tokens for the user are deleted before a new one is issued |

### Email Delivery (Deferred — Phase 14)

In Phase 14, the raw reset token is **logged at INFO** only:

```
DEV: password reset email would be sent to=<email> token=<rawToken> expiresInMinutes=30
```

To use the reset flow locally during development:
1. Trigger forgot-password for your user's email.
2. Copy the raw token from the backend log.
3. Navigate to `/reset-password?token=<rawToken>`.

**TODO(email-infra)**: Replace the log call in `PasswordResetService.requestReset()` with
a `PasswordResetEmailEvent` published post-commit and consumed by a new
`PasswordResetEmailListener` — following the same pattern as `PwsOfferEmailListener`
from the 2026-04-13 ADR.

### Password Constraints

- Minimum 8 characters (enforced by Bean Validation on `ResetPasswordRequest`).
- Hashed with BCrypt (Spring `BCryptPasswordEncoder`), same encoder used for login.

### Related Files

| File | Purpose |
|------|---------|
| `PasswordResetService.java` | Core logic: token issuance + confirmation |
| `PasswordResetToken.java` | JPA entity |
| `PasswordResetTokenRepository.java` | Data access |
| `AuthController.java` | `/forgot-password` and `/reset-password` endpoints |
| `V75__auth_password_reset_tokens.sql` | Schema |
| `ForgotPasswordForm.tsx` | Frontend form (`/forgot-password`) |
| `ResetPasswordForm.tsx` | Frontend form (`/reset-password?token=…`) |
