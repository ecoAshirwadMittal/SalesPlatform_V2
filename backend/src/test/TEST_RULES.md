# Test Rules — Derived from PR Review (2026-04-07)

## CRITICAL Security Tests

### SEC-01: No auth bypass via userId query param
- `/api/v1/auth/me` MUST reject unauthenticated requests (HTTP 401)
- `/api/v1/auth/buyer-codes` MUST reject unauthenticated requests (HTTP 401)
- Both endpoints MUST extract userId from JWT, never from query param

### SEC-02: Real JWT tokens
- Login MUST return a signed JWT (HS256+) with claims: sub, userId, roles, exp
- Token MUST expire (default 24h, rememberMe 7d)
- Invalid/expired tokens MUST return 401

### SEC-03: SecurityFilterChain enforces authentication
- Public endpoints: `/api/v1/auth/login`, `/api/v1/auth/sso`, `/actuator/health` — permitAll
- All other endpoints MUST require valid JWT Bearer token
- Missing/invalid Authorization header → 401
- Role-based: `/api/v1/admin/**` requires ADMIN role

### SEC-04: No JSON injection in Oracle payload
- `prepareOraclePayload()` MUST use ObjectMapper, not StringBuilder
- Buyer codes with `"`, `\`, `}` chars MUST be safely serialized
- SKUs with special chars MUST not break JSON structure

### SEC-05: No hardcoded credentials in committed config
- `application.yml` base profile MUST use `${DB_USERNAME}` / `${DB_PASSWORD}` env vars
- Only `local` profile may have fallback defaults
- `pom.xml` Flyway plugin MUST use `${env.DB_*}` properties

### SEC-06: Oracle password MUST be encrypted
- `OracleConfigController.updateConfig()` MUST encrypt password before storing
- GET response MUST never expose password (already correct)

### SEC-07: Stack traces NOT exposed in base profile
- `include-exception: true` MUST be scoped to `local` profile only
- Base profile: `include-exception: false`, `include-message: never`
- Actuator: only expose `health,info` (not `mappings`, `flyway`)
- Health details: `show-details: when-authorized`

## CRITICAL Error Handling Tests

### ERR-01: Global exception handler exists
- Unhandled RuntimeException → 500 with `{error, message}` JSON (no stack trace)
- EntityNotFoundException → 404
- MethodArgumentNotValidException → 400 with field errors
- AccessDeniedException → 403
- AuthenticationException → 401

### ERR-02: @Transactional on multi-query reads
- `DirectUserService.getDirectUsers()` MUST have `@Transactional(readOnly = true)`

## HIGH Code Quality Tests

### HI-01: @Valid on all @RequestBody parameters
- Every controller `@RequestBody` MUST be paired with `@Valid`
- `CartItemRequest` MUST have validation annotations (sku @NotBlank, quantity @NotNull @Min(0))
- Invalid requests → 400 with structured error response

### HI-02: No repository injection in controllers
- `InventoryController` MUST delegate `case-lots` to service layer
- Controllers should only depend on Service classes

### HI-03: No N+1 queries in OfferReviewService
- `getStatusSummaries()` MUST use aggregate queries (COUNT/SUM) not load full entities
- `listOffers()` MUST not trigger lazy collection loads per-offer

### HI-04: Unbounded list endpoints must paginate
- `/api/v1/inventory/devices` returning 22k+ rows MUST support Pageable

### HI-05: No CascadeType.ALL risks verified
- Deleting an Offer MUST NOT cascade-delete related Orders

## MEDIUM Tests

### MED-01: generateOfferNumber() race condition
- Concurrent offer submissions MUST not produce duplicate offer numbers

### MED-02: Shared ObjectMapper bean
- Services MUST inject ObjectMapper bean, not create `new ObjectMapper()` per class

### MED-03: Modern Java idioms
- Use `Stream.toList()` instead of `Collectors.toList()` (Java 21)
- Return `Optional<T>` instead of nullable for single-entity lookups
