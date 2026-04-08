# Utility: index.ts

- **Path**: `src\utils\clients\index.ts`
- **Category**: Utility
- **Lines**: 9
- **Size**: 323 bytes

## Source Code

```typescript
/**
 * Client barrel export
 * All database and service clients re-exported from a single entry point.
 */

export { SnowflakeSecretsClient, SECRETS_ARN_MAP } from './SnowflakeSecretsClient';
export { SnowflakeClient, getSnowflakeClient } from './SnowflakeClient';
export type { SnowflakeConfig } from './SnowflakeClient';

```
