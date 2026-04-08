# Utility: SnowflakeSecretsClient.ts

- **Path**: `src\utils\clients\SnowflakeSecretsClient.ts`
- **Category**: Utility
- **Lines**: 128
- **Size**: 4,614 bytes
- **Members**: `class SnowflakeSecretsClient`

## Source Code

```typescript
import {
    SecretsManagerClient,
    GetSecretValueCommand
} from '@aws-sdk/client-secrets-manager';

/**
 * Snowflake Secrets Client
 * 
 * Minimal, Snowflake-scoped AWS Secrets Manager client.
 * Fetches Snowflake credentials (including private key) from AWS Secrets Manager.
 * 
 * This is intentionally NOT a global AWS client — it only handles Secrets Manager
 * for the purpose of retrieving Snowflake connection details.
 * 
 * Required Environment Variables:
 *   - AWS_ACCESS_KEY_ID
 *   - AWS_SECRET_ACCESS_KEY
 *   - AWS_SESSION_TOKEN (required for temporary/SSO credentials)
 *   - ENV (optional, defaults to 'qa') — controls which secret ARN is used
 */

/** Map of environment names to their Secrets Manager ARNs */
export const SECRETS_ARN_MAP: Record<string, string> = {
    qa: 'arn:aws:secretsmanager:us-west-2:743861740499:secret:qa-sdet-env-var-w0xBJr',
    prod: 'arn:aws:secretsmanager:us-west-2:743861740499:secret:prod-sdet-env-var-Wjed8O',
    dev: 'arn:aws:secretsmanager:us-west-2:743861740499:secret:dev-sdet-env-var-JQm4bv'
};

export class SnowflakeSecretsClient {
    private readonly config: {
        region: string;
        credentials: {
            accessKeyId: string;
            secretAccessKey: string;
            sessionToken?: string;
        };
    };

    constructor(region: string = 'us-west-2') {
        const accessKeyId = process.env.AWS_ACCESS_KEY_ID || '';
        const secretAccessKey = process.env.AWS_SECRET_ACCESS_KEY || '';
        const sessionToken = process.env.AWS_SESSION_TOKEN;

        if (!accessKeyId || !secretAccessKey) {
            throw new Error(
                'Missing AWS credentials: AWS_ACCESS_KEY_ID and AWS_SECRET_ACCESS_KEY must be set.'
            );
        }

        this.config = {
            region,
            credentials: {
                accessKeyId,
                secretAccessKey,
                ...(sessionToken && { sessionToken })
            }
        };
    }

    /**
     * Retrieve all secrets from a given ARN.
     * @param secretArn - The ARN of the secret to retrieve
     * @returns Parsed JSON object of all key-value pairs in the secret
     */
    async getSecrets(secretArn: string): Promise<Record<string, string>> {
        const client = new SecretsManagerClient(this.config);

        if (!secretArn) {
            throw new Error('Secret ARN is required to retrieve secrets.');
        }

        try {
            const command = new GetSecretValueCommand({ SecretId: secretArn });
            const response = await client.send(command);

            if (!response.SecretString) {
                throw new Error('Failed to retrieve secrets: SecretString is empty.');
            }

            // Sanitize control characters inside JSON string values
            // PEM keys stored in SM often contain literal newlines that break JSON.parse
            // This regex matches content between double quotes and escapes control chars within
            const sanitized = response.SecretString.replace(
                /"([^"\\]|\\.)*"/gs,
                (match) => match
                    .replace(/\r\n/g, '\\n')
                    .replace(/\r/g, '\\n')
                    .replace(/\n/g, '\\n')
                    .replace(/\t/g, '\\t')
            );

            return JSON.parse(sanitized);
        } catch (error: any) {
            console.error(`Failed to retrieve AWS Secrets: ${error.message}`);
            throw error;
        }
    }

    /**
     * Retrieve Snowflake-specific secrets for the current environment.
     * Uses ENV env var (defaults to 'qa') to select the correct ARN.
     * 
     * @returns All secrets from the environment's secret store
     */
    async getSnowflakeSecrets(): Promise<Record<string, string>> {
        const env = (process.env.ENV || 'qa').toLowerCase();
        const secretArn = SECRETS_ARN_MAP[env];

        if (!secretArn) {
            throw new Error(
                `No secret ARN configured for environment: "${env}". ` +
                `Valid environments: ${Object.keys(SECRETS_ARN_MAP).join(', ')}`
            );
        }

        console.log(`🔑 Fetching Snowflake secrets for environment: ${env}`);
        const allSecrets = await this.getSecrets(secretArn);

        // Filter to only Snowflake-related keys for logging purposes
        const snowflakeKeys = Object.keys(allSecrets).filter(
            key => key.toUpperCase().startsWith('SNOWFLAKE')
        );
        console.log(`✅ Retrieved ${snowflakeKeys.length} Snowflake-related keys from Secrets Manager`);

        return allSecrets;
    }
}

```
