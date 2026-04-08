# Test Spec: SnowflakeConnectivityTest.spec.ts

- **Path**: `src\tests\PremiumWholesales\PWS_WorkflowsTests\SnowflakeConnectivityTest.spec.ts`
- **Category**: Test Spec
- **Lines**: 80
- **Size**: 2,885 bytes

## Source Code

```typescript
import { test, expect } from '@playwright/test';
import * as dotenv from 'dotenv';
dotenv.config({ path: '.env.secrets' });
dotenv.config();

/**
 * Snowflake Connectivity Verification
 * 
 * Standalone test to verify that:
 * 1. Secrets Manager successfully fetched Snowflake credentials (via global-setup)
 * 2. The expected SNOWFLAKE_* environment variables are present
 * 
 * Run: npx playwright test SnowflakeConnectivityTest
 */
test.describe('Snowflake Connectivity Verification', () => {

    test('SFCONN-001: Verify Snowflake environment variables are loaded', async () => {
        console.log('=== Snowflake Environment Variable Check ===');

        const requiredVars = [
            'SNOWFLAKE_ACCOUNT',
            'SNOWFLAKE_WAREHOUSE',
            'SNOWFLAKE_DATABASE',
            'SNOWFLAKE_SCHEMA',
            'SNOWFLAKE_ROLE'
        ];

        // At least one auth method should be present
        const authVars = {
            password: ['SNOWFLAKE_USERNAME', 'SNOWFLAKE_PASSWORD'],
            privateKey: ['SNOWFLAKE_PRIVATE_KEY'],
            oauth: ['SNOWFLAKE_CLIENT_ID', 'SNOWFLAKE_CLIENT_SECRET']
        };

        const missingVars: string[] = [];
        const presentVars: string[] = [];

        for (const varName of requiredVars) {
            if (process.env[varName]) {
                presentVars.push(varName);
                console.log(`${varName} = ${varName.includes('KEY') || varName.includes('SECRET') ? '***' : process.env[varName]}`);
            } else {
                missingVars.push(varName);
                console.log(`${varName} = NOT SET`);
            }
        }

        // Check auth methods
        let authMethodFound = false;
        for (const [method, vars] of Object.entries(authVars)) {
            const allPresent = vars.every(v => !!process.env[v]);
            if (allPresent) {
                authMethodFound = true;
                console.log(`Auth method: ${method}`);
                break;
            }
        }

        if (!authMethodFound) {
            console.log(`No auth method found. Need one of: password (USERNAME+PASSWORD), privateKey (PRIVATE_KEY), oauth (CLIENT_ID+CLIENT_SECRET)`);
        }

        console.log(`\n=== Summary: ${presentVars.length}/${requiredVars.length} required vars present ===`);

        if (missingVars.length > 0) {
            console.log(`Missing: ${missingVars.join(', ')}`);
            console.log('Did global-setup run? Check AWS credentials in .env');
        }

        // Soft assert — don't fail the entire suite, just report findings
        expect.soft(missingVars.length,
            `Missing required Snowflake env vars: ${missingVars.join(', ')}`
        ).toBe(0);

        expect.soft(authMethodFound,
            'At least one Snowflake auth method (password, privateKey, oauth) must be configured'
        ).toBeTruthy();
    });
});

```
