# Utility: commonMethods.ts

- **Path**: `src\utils\helpers\commonMethods.ts`
- **Category**: Utility
- **Lines**: 83
- **Size**: 3,171 bytes
- **Members**: `Logger`

## Source Code

```typescript
import { Page, expect, Locator } from "playwright/test";
import pino from "pino";


export async function click(page: Page, element: Locator | string, timeout: number = 5000): Promise<void> {
    const locator = typeof element === "string" ? page.locator(element) : element;
    await locator.waitFor({ state: 'visible', timeout });
    await locator.evaluate((el: HTMLElement) => {
        el.style.boxShadow = '0 0 0 4px #ff0000 inset';
        el.style.transition = 'box-shadow 0.2s';
    });
    await page.waitForTimeout(300);
    await locator.click();
    await locator.evaluate((el: HTMLElement) => {
        el.style.boxShadow = '';
    });
}

export async function waitAndClick(page: Page, selector: string | Locator, timeout: number = 5000): Promise<void> {
    const element = typeof selector === "string" ? page.locator(selector) : selector;
    await element.waitFor({ state: 'visible', timeout });
    // Highlight the element before clicking (red color)
    await element.evaluate((el: HTMLElement) => {
        el.style.boxShadow = '0 0 0 4px #ff0000 inset';
        el.style.transition = 'box-shadow 0.2s';
    });
    await page.waitForTimeout(300); // Give time for highlight to show
    await element.click();
    // Remove highlight after click
    await element.evaluate((el: HTMLElement) => {
        el.style.boxShadow = '';
    });
}

export async function waitAndFill(page: Page, selector: string | Locator, value: string, timeout: number = 10000): Promise<void> {
    const element = typeof selector === "string" ? page.locator(selector) : selector;
    await element.waitFor({ state: 'visible', timeout });
    // Highlight the element before filling (red color)
    await element.evaluate((el: HTMLElement) => {
        el.style.boxShadow = '0 0 0 4px #ff0000 inset';
        el.style.transition = 'box-shadow 0.2s';
    });
    await page.waitForTimeout(300); // Give time for highlight to show
    await element.fill(value);
    // Remove highlight after fill
    await element.evaluate((el: HTMLElement) => {
        el.style.boxShadow = '';
    });
}

export async function waitAndGetText(page: Page, selector: string | Locator, timeout: number = 5000): Promise<string> {
    const element = typeof selector === "string" ? page.locator(selector) : selector;
    await element.waitFor({ state: 'visible', timeout });
    // Highlight the element before getting text (red color)
    await element.evaluate((el: HTMLElement) => {
        el.style.boxShadow = '0 0 0 4px #ff0000 inset';
        el.style.transition = 'box-shadow 0.2s';
    });
    await page.waitForTimeout(300); // Give time for highlight to show
    const text = (await element.textContent())?.trim() ?? '';
    // Remove highlight after getting text
    await element.evaluate((el: HTMLElement) => {
        el.style.boxShadow = '';
    });
    return text;
}

export const logger = pino({
    level: "info",
    transport: {
        target: 'pino-pretty',
        options: {
            colorize: true,
            translateTime: "HH:MM:ss Z",
            ignore: "pid, hostname"
        }
    }
})

export function Logger(message: string) {
    logger.info(message);
}

```
