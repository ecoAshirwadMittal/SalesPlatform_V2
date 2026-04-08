# Utility: deposcoTestDataHelper.ts

- **Path**: `src\utils\helpers\deposcoTestDataHelper.ts`
- **Category**: Utility
- **Lines**: 74
- **Size**: 2,047 bytes
- **Members**: `getDeposcoCredentials`, `getDeposcoUrl`, `getTestScenario`, `getBatchTestOrders`

## Source Code

```typescript
import deposcoTestData from '../resources/deposco_test_data.json';

/**
 * Deposco Test Data Helper
 * Provides easy access to test data with type safety
 */

export interface DeposcoCredentials {
    username: string;
    password: string;
}

export interface TestScenario {
    description: string;
    orderNumbers: string[];
}

/**
 * Get Deposco credentials
 * @param profile - Credential profile name (default: 'default')
 */
export function getDeposcoCredentials(profile: string = 'default'): DeposcoCredentials {
    const credentials = deposcoTestData.deposco.credentials[profile as keyof typeof deposcoTestData.deposco.credentials];
    if (!credentials) {
        throw new Error(`Deposco credential profile '${profile}' not found`);
    }
    return credentials;
}

/**
 * Get Deposco URL
 */
export function getDeposcoUrl(): string {
    return deposcoTestData.deposco.url;
}

/**
 * Get order number for a specific test type
 * @param testType - The test type (e.g., 'integration_single', 'shipOrder_single')
 */
export function getOrderNumber(testType: keyof typeof deposcoTestData.orderNumbers): string | string[] {
    return deposcoTestData.orderNumbers[testType];
}

/**
 * Get test scenario by name
 * @param scenarioName - The scenario name (e.g., 'integration', 'shipOrder', 'smoke')
 */
export function getTestScenario(scenarioName: keyof typeof deposcoTestData.testScenarios): TestScenario {
    return deposcoTestData.testScenarios[scenarioName];
}

/**
 * Get order numbers for batch testing
 */
export function getBatchTestOrders(): string[] {
    return deposcoTestData.orderNumbers.batch_test_orders;
}

/**
 * Get all test data (for custom usage)
 */
export function getAllTestData(): typeof deposcoTestData {
    return deposcoTestData;
}

// Default export with commonly used values
export default {
    credentials: getDeposcoCredentials(),
    url: getDeposcoUrl(),
    integrationOrderNumber: deposcoTestData.orderNumbers.integration_single,
    shipOrderNumber: deposcoTestData.orderNumbers.shipOrder_single
};

```
