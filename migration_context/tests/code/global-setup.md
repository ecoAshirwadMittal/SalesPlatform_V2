# Global Setup: global-setup.ts

- **Path**: `global-setup.ts`
- **Category**: Global Setup
- **Lines**: 92
- **Size**: 4,050 bytes
- **Members**: `globalSetup`

## Source Code

```typescript
import 'dotenv/config';
import { SnowflakeSecretsClient } from './src/utils/clients/SnowflakeSecretsClient';
import * as fs from 'fs';
import * as path from 'path';

/**
 * Playwright Global Setup
 * 
 * Runs ONCE before all test workers start.
 * Fetches Snowflake credentials from AWS Secrets Manager and persists them
 * to .env.secrets so that each parallel worker can load them independently.
 * 
 * Flow:
 *   1. Read AWS creds from .env (AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, etc.)
 *   2. Fetch all secrets from the configured ARN (based on ENV=qa|prod|dev)
 *   3. Write secrets to .env.secrets as KEY=VALUE pairs
 *   4. Also inject into current process.env for the setup process itself
 * 
 * Worker-side loading:
 *   Each test file or BaseTest should call:
 *     import * as dotenv from 'dotenv';
 *     dotenv.config({ path: '.env.secrets' });
 */
async function globalSetup() {
    const secretsEnvPath = path.join(process.cwd(), '.env.secrets');

    // Skip Secrets Manager fetch if AWS creds are not configured
    // This allows local development with manual .env.secrets
    if (!process.env.AWS_ACCESS_KEY_ID || !process.env.AWS_SECRET_ACCESS_KEY) {
        console.log('⚠️  AWS credentials not found — skipping Secrets Manager fetch.');
        console.log('   To use Secrets Manager, set AWS_ACCESS_KEY_ID, AWS_SECRET_ACCESS_KEY, and optionally AWS_SESSION_TOKEN in your .env');

        if (fs.existsSync(secretsEnvPath)) {
            console.log('ℹ️  Using existing .env.secrets file for credentials.');
        } else {
            console.log('⚠️  No .env.secrets file found. Snowflake tests will fail unless credentials are set manually.');
        }
        return;
    }

    try {
        console.log('🔑 Global Setup: Fetching secrets from AWS Secrets Manager...');
        const secretsClient = new SnowflakeSecretsClient();
        const allSecrets = await secretsClient.getSnowflakeSecrets();

        // Write all secrets to .env.secrets for cross-worker persistence
        const envContent = Object.entries(allSecrets)
            .map(([key, value]) => `${key}=${value}`)
            .join('\n');

        fs.writeFileSync(secretsEnvPath, envContent, 'utf-8');
        console.log(`📝 Secrets written to ${secretsEnvPath}`);

        // Also inject into current process.env for this setup process
        for (const [key, value] of Object.entries(allSecrets)) {
            process.env[key] = value as string;
        }

        // ── Map SP-specific keys → standard SNOWFLAKE_ env vars ──
        const snowflakeDefaults: Record<string, string> = {
            SNOWFLAKE_ACCOUNT: 'xk09431-ecoatm',
            SNOWFLAKE_USERNAME: 'MENDIX_SRVC_QA',
            SNOWFLAKE_DATABASE: 'ECO_QA',
            SNOWFLAKE_SCHEMA: 'AUCTIONS',
            SNOWFLAKE_ROLE: 'ROLE_ECO_QA_READ_WRITE',
            SNOWFLAKE_WAREHOUSE: 'DW_PROD_WH',
        };

        // Map the private key from the SP-prefixed secret name
        if (allSecrets['SP_SNOWFLAKE_QA_KEY']) {
            snowflakeDefaults['SNOWFLAKE_PRIVATE_KEY'] = allSecrets['SP_SNOWFLAKE_QA_KEY'];
        }

        // Set in process.env and append to .env.secrets
        const snowflakeEnvLines: string[] = ['\n# ── Snowflake Connection Defaults ──'];
        for (const [key, value] of Object.entries(snowflakeDefaults)) {
            process.env[key] = value;
            snowflakeEnvLines.push(`${key}=${value}`);
        }
        fs.appendFileSync(secretsEnvPath, snowflakeEnvLines.join('\n') + '\n', 'utf-8');
        console.log(`❄️  Snowflake defaults appended to ${secretsEnvPath}`);

        console.log('✅ Global Setup complete — Snowflake secrets loaded.');
    } catch (error: any) {
        console.error(`❌ Global Setup failed: ${error.message}`);
        console.error('   Snowflake tests will fail. Check AWS credentials and network connectivity.');
        // Don't throw — allow tests to run; they'll fail individually with clear error messages
    }
}

export default globalSetup;

```
