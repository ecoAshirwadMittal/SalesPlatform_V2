# Page Object: DeposcoAPI.ts

- **Path**: `src\pages\PWS\AdminPages\DeposcoAPI.ts`
- **Category**: Page Object
- **Lines**: 134
- **Size**: 5,692 bytes
- **Members**: `class DeposcoAPI`

## Source Code

```typescript
import { APIRequestContext, request } from '@playwright/test';
import { Logger } from '../../../utils/helpers/data_utils';
import 'dotenv/config';

const CLIENT_ID = process.env.CLIENT_ID!;
const CLIENT_SECRET = process.env.CLIENT_SECRET!;
const REFRESH_TOKEN = process.env.REFRESH_TOKEN!;
const AUTH_URL = process.env.AUTH_URL!;
const ORDER_URL = process.env.ORDER_URL!;
const INVENTORY_URL = process.env.INVENTORY_URL!;
const DeposcoV1_username = process.env.DEPOSCO_V1_USERNAME!;
const DeposcoV1_password = process.env.DEPOSCO_V1_PASSWORD!;

export class DeposcoAPI {

    private async getAccessTokenWithRefresh(): Promise<string> {
        const apiContext: APIRequestContext = await request.newContext();
        const response = await apiContext.post(AUTH_URL, {
            headers: {
                "Content-Type": "application/x-www-form-urlencoded",
                Authorization: "Basic " + Buffer.from(`${CLIENT_ID}:${CLIENT_SECRET}`).toString("base64"),
            },
            form: {
                grant_type: "refresh_token",
                refresh_token: REFRESH_TOKEN,
            },
        });
        if (!response.ok()) {
            throw new Error(`Failed to fetch token: ${response.status()} ${response.statusText()}`);
        }
        const data = await response.json();
        return data.access_token;
    }

    async getAvailableToPromiseFromDeposco(itemNumber: string): Promise<number | null> {
        const apiContext: APIRequestContext = await request.newContext({
            httpCredentials: {
                username: DeposcoV1_username,
                password: DeposcoV1_password
            }
        });
        const response = await apiContext.get(INVENTORY_URL, {
            headers: {
                "Accept": "application/json"
            }
        });
        if (response.status() !== 200) {
            console.warn(`Inventory API returned status ${response.status()}`);
            const textBody = await response.text();
            console.warn("Response body:", textBody); // Log XML or error message
            return null;
        }
        const contentType = response.headers()['content-type'];
        if (!contentType || !contentType.includes('application/json')) {
            const textBody = await response.text();
            console.warn("Non-JSON response body:", textBody);
            return null;
        }
        const data = await response.json();
        if (!Array.isArray(data.itemInventory)) return null;
        for (const item of data.itemInventory) {
            if (item.itemNumber === itemNumber) {
                for (const facility of item.facilityInventory) {
                    if (facility.inventory && facility.inventory.availableToPromise !== undefined) {
                        const atp = Number(facility.inventory.availableToPromise);
                        return Math.floor(atp);
                    }
                }
            }
        }
        return null;
    }

    async getOrderStatusCode(orderNumber: string): Promise<number> {
        const accessToken = await this.getAccessTokenWithRefresh();
        const apiContext: APIRequestContext = await request.newContext();
        const fullUrl = `${ORDER_URL}${orderNumber}`;
        const response = await apiContext.get(fullUrl, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                "Content-Type": "application/json"
            }
        });
        return response.status();
    }

    async getOrderStatus(orderNumber: string): Promise<string> {
        const accessToken = await this.getAccessTokenWithRefresh();
        const apiContext: APIRequestContext = await request.newContext();
        const fullUrl = `${ORDER_URL}${orderNumber}`;
        const response = await apiContext.get(fullUrl, {
            headers: {
                Authorization: `Bearer ${accessToken}`,
                "Content-Type": "application/json"
            }
        });
        if (!response.ok()) {
            throw new Error(`Failed to fetch order ${orderNumber}. Status: ${response.status()}`);
        }
        const json = await response.json();
        const orderStatus = json?.data?.[0]?.orderStatus ?? "Unknown";
        Logger(`Order ${orderNumber} status: ${orderStatus}`);
        return orderStatus;
    }

    async getOrderStatusTrial(orderNumber: string, delayMs = 2000): Promise<string> {
        const accessToken = await this.getAccessTokenWithRefresh();
        const apiContext: APIRequestContext = await request.newContext();
        const fullUrl = `${ORDER_URL}${orderNumber}`;
        const timeoutMs = 60_000; // 1 minute
        const startTime = Date.now();
        while (Date.now() - startTime < timeoutMs) {
            const response = await apiContext.get(fullUrl, {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                    "Content-Type": "application/json"
                }
            });
            if (!response.ok()) {
                throw new Error(`Failed to fetch order ${orderNumber}. Status: ${response.status()}`);
            }
            const json = await response.json();
            if (json?.data?.length > 0 && json.data[0].orderStatus) {
                const orderStatus = json.data[0].orderStatus;
                Logger(`Order ${orderNumber} status: ${orderStatus}`);
                return orderStatus;
            }
            Logger(`Order ${orderNumber} not yet available in Deposco. Retrying...`);
            await new Promise(res => setTimeout(res, delayMs));
        }
        throw new Error(`Order ${orderNumber} not found in Deposco after 1 minute.`);
    }
}

```
