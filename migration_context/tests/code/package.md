# Config: package.json

- **Path**: `package.json`
- **Category**: Config
- **Lines**: 36
- **Size**: 893 bytes

## Source Code

```json
{
  "name": "qa-playwright-salesplatform",
  "version": "1.0.0",
  "description": "",
  "main": "index.js",
  "scripts": {
    "testError": "echo \"Error: no test specified\" && exit 1",
    "test": "npx playwright test"
  },
  "keywords": [],
  "author": "",
  "license": "ISC",
  "devDependencies": {
    "@playwright/test": "^1.52.0",
    "@serenity-js/core": "^3.31.6",
    "@serenity-js/playwright": "^3.31.6",
    "@types/node": "^22.10.7",
    "allure-commandline": "^2.33.0",
    "allure-playwright": "^3.2.0"
  },
  "dependencies": {
    "@aws-sdk/client-secrets-manager": "^3.985.0",
    "axios": "^1.13.4",
    "csv-parser": "^3.2.0",
    "dotenv": "^17.2.1",
    "fast-csv": "^5.0.2",
    "node-fetch": "^2.7.0",
    "pino": "^9.6.0",
    "pino-pretty": "^13.0.0",
    "playwright": "^1.50.1",
    "snowflake-sdk": "^2.3.4",
    "typescript": "^5.7.3",
    "xlsx": "^0.18.5"
  }
}

```
