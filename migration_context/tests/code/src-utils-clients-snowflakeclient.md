# Utility: SnowflakeClient.ts

- **Path**: `src\utils\clients\SnowflakeClient.ts`
- **Category**: Utility
- **Lines**: 227
- **Size**: 9,277 bytes
- **Members**: `class SnowflakeClient`, `isConnected`, `getSnowflakeClient`

## Source Code

```typescript
import snowflake from 'snowflake-sdk';
import * as crypto from 'crypto';

/**
 * Snowflake Client — Key-Pair Authentication
 *
 * Connects to Snowflake using an encrypted RSA private key stored in
 * AWS Secrets Manager (fetched via global-setup → .env.secrets).
 *
 * Default connection targets the QA Auctions schema:
 *   Account:   xk09431-ecoatm
 *   Database:  ECO_QA
 *   Schema:    AUCTIONS
 *   Role:      ROLE_ECO_QA_READ_WRITE
 *   Warehouse: DW_PROD_WH
 *
 * Usage:
 *   import { getSnowflakeClient } from '../utils/clients';
 *   const sf = getSnowflakeClient();
 *   await sf.connect();
 *   const rows = await sf.query('SELECT * FROM MY_TABLE LIMIT 10');
 *   await sf.disconnect();
 */

// ────────────────────────────────────────────────────────────────
// Types
// ────────────────────────────────────────────────────────────────

export interface SnowflakeConfig {
    account: string;
    username: string;
    database: string;
    schema: string;
    role: string;
    warehouse: string;
    privateKey?: string;
    privateKeyPass?: string;
}

// ────────────────────────────────────────────────────────────────
// Client
// ────────────────────────────────────────────────────────────────

export class SnowflakeClient {
    private connection: snowflake.Connection | null = null;
    private readonly config: SnowflakeConfig;

    constructor(config?: Partial<SnowflakeConfig>) {
        this.config = {
            account: config?.account ?? process.env.SNOWFLAKE_ACCOUNT ?? 'xk09431-ecoatm',
            username: config?.username ?? process.env.SNOWFLAKE_USERNAME ?? 'MENDIX_SRVC_QA',
            database: config?.database ?? process.env.SNOWFLAKE_DATABASE ?? 'ECO_QA',
            schema: config?.schema ?? process.env.SNOWFLAKE_SCHEMA ?? 'AUCTIONS',
            role: config?.role ?? process.env.SNOWFLAKE_ROLE ?? 'ROLE_ECO_QA_READ_WRITE',
            warehouse: config?.warehouse ?? process.env.SNOWFLAKE_WAREHOUSE ?? 'DW_PROD_WH',
            privateKey: config?.privateKey ?? process.env.SNOWFLAKE_PRIVATE_KEY ?? process.env.SP_SNOWFLAKE_QA_KEY ?? '',
            privateKeyPass: config?.privateKeyPass ?? process.env.SNOWFLAKE_PRIVATE_KEY_PASS ?? '',
        };
    }

    // ── Connection lifecycle ──────────────────────────────────────

    /**
     * Establish a connection to Snowflake using key-pair authentication.
     * The encrypted PEM key is decrypted in-memory and passed to the SDK.
     */
    async connect(): Promise<void> {
        if (this.connection) {
            console.log('⚠️  Snowflake: already connected.');
            return;
        }

        const { account, username, database, schema, role, warehouse, privateKey, privateKeyPass } = this.config;

        if (!privateKey) {
            throw new Error(
                'Snowflake private key not found. ' +
                'Ensure SNOWFLAKE_PRIVATE_KEY or SP_SNOWFLAKE_QA_KEY is set (via global-setup / .env.secrets).'
            );
        }

        // Decrypt the encrypted private key and re-export as unencrypted PEM PKCS8.
        // The key from AWS SM is raw base64 (no PEM headers), so we wrap it first.
        let unencryptedPem: string;
        try {
            // If the key doesn't already have PEM headers, wrap it
            let pemKey = privateKey;
            if (!privateKey.includes('-----BEGIN')) {
                const formatted = privateKey.match(/.{1,64}/g)?.join('\n') || privateKey;
                pemKey = `-----BEGIN ENCRYPTED PRIVATE KEY-----\n${formatted}\n-----END ENCRYPTED PRIVATE KEY-----`;
            }

            const privateKeyObject = crypto.createPrivateKey({
                key: pemKey,
                format: 'pem',
                passphrase: privateKeyPass ?? '',
            });

            // Re-export as unencrypted PKCS8 PEM — this is the format snowflake-sdk expects
            unencryptedPem = privateKeyObject.export({ type: 'pkcs8', format: 'pem' }) as string;
        } catch (err: any) {
            throw new Error(`Failed to parse Snowflake private key: ${err.message}`);
        }

        // Configure the SDK
        snowflake.configure({ logLevel: 'WARN' });

        this.connection = snowflake.createConnection({
            account,
            username,
            database,
            schema,
            role,
            warehouse,
            authenticator: 'SNOWFLAKE_JWT',
            privateKey: unencryptedPem,
        });

        return new Promise<void>((resolve, reject) => {
            this.connection!.connect((err) => {
                if (err) {
                    this.connection = null;
                    reject(new Error(`Snowflake connection failed: ${err.message}`));
                } else {
                    console.log(`✅ Snowflake connected — ${database}.${schema} as ${role}`);
                    resolve();
                }
            });
        });
    }

    /** Disconnect from Snowflake. Safe to call even if not connected. */
    async disconnect(): Promise<void> {
        if (!this.connection) return;
        return new Promise<void>((resolve) => {
            this.connection!.destroy((err) => {
                if (err) console.warn(`⚠️  Snowflake disconnect warning: ${err.message}`);
                this.connection = null;
                console.log('🔌 Snowflake disconnected.');
                resolve();
            });
        });
    }

    /** Check if currently connected. */
    isConnected(): boolean {
        return this.connection !== null && this.connection.isUp();
    }

    // ── Query methods ─────────────────────────────────────────────

    /**
     * Execute a SQL query and return all rows.
     * Auto-connects if not already connected.
     */
    async query<T = Record<string, any>>(sqlText: string, binds?: any[]): Promise<T[]> {
        await this.ensureConnected();
        return new Promise<T[]>((resolve, reject) => {
            this.connection!.execute({
                sqlText,
                binds,
                complete: (err, _stmt, rows) => {
                    if (err) reject(new Error(`Snowflake query error: ${err.message}`));
                    else resolve((rows || []) as T[]);
                },
            });
        });
    }

    /** Execute a SQL query and return only the first row, or null. */
    async queryOne<T = Record<string, any>>(sqlText: string, binds?: any[]): Promise<T | null> {
        const rows = await this.query<T>(sqlText, binds);
        return rows.length > 0 ? rows[0] : null;
    }

    /** Execute a SQL query and return the first column of the first row. */
    async queryScalar<T = any>(sqlText: string, binds?: any[]): Promise<T | null> {
        const row = await this.queryOne<Record<string, any>>(sqlText, binds);
        if (!row) return null;
        const firstKey = Object.keys(row)[0];
        return row[firstKey] as T;
    }

    // ── Context switching ─────────────────────────────────────────

    async useWarehouse(warehouse: string): Promise<void> {
        await this.query(`USE WAREHOUSE ${warehouse}`);
    }

    async useDatabase(database: string): Promise<void> {
        await this.query(`USE DATABASE ${database}`);
    }

    async useSchema(schema: string): Promise<void> {
        await this.query(`USE SCHEMA ${schema}`);
    }

    async useRole(role: string): Promise<void> {
        await this.query(`USE ROLE ${role}`);
    }

    // ── Internals ─────────────────────────────────────────────────

    private async ensureConnected(): Promise<void> {
        if (!this.isConnected()) {
            await this.connect();
        }
    }
}

// ────────────────────────────────────────────────────────────────
// Convenience factory
// ────────────────────────────────────────────────────────────────

let _singleton: SnowflakeClient | null = null;

/**
 * Get a shared SnowflakeClient instance.
 * Creates a new one on first call; reuses on subsequent calls.
 */
export function getSnowflakeClient(config?: Partial<SnowflakeConfig>): SnowflakeClient {
    if (!_singleton) {
        _singleton = new SnowflakeClient(config);
    }
    return _singleton;
}

```
