import { test, expect } from "@playwright/test";
import path from "path";
import { isBackendAvailable } from "./_helpers/backend";

async function loginAs(page, email: string, password: string) {
  await page.goto("/login");
  await page.fill('[name="email"]', email);
  await page.fill('[name="password"]', password);
  await page.click('button[type="submit"]');
  await page.waitForURL((u) => !u.pathname.includes("/login"));
}

test.describe("Admin → Purchase Orders", () => {
  test.beforeAll(async () => {
    test.skip(!(await isBackendAvailable()), "requires Spring Boot backend on :8080");
  });

  test("administrator: list → open detail → upload Excel", async ({ page }) => {
    await loginAs(page, "admin@test.com", "Admin123!");
    await page.goto("/admin/auctions-data-center/purchase-orders");
    await expect(page.locator("h1")).toHaveText("Purchase Orders");

    const firstRow = page.locator("tbody tr").first();
    await expect(firstRow).toBeVisible();

    await firstRow.locator('a:has-text("Edit")').click();
    await expect(page.locator("h1")).toContainText("PO #");

    page.on("dialog", (d) => d.accept());
    await page.setInputFiles(
      'input[type="file"]',
      path.join(__dirname, "..", "fixtures", "po-upload-sample.xlsx"),
    );
    await expect(page.locator("table tbody tr")).toHaveCount(20, { timeout: 10_000 });

    await expect(page.locator('a:has-text("Download Excel")')).toBeVisible();
  });

  test("salesops: can list", async ({ page }) => {
    await loginAs(page, "salesops@test.com", "SalesOps123!");
    await page.goto("/admin/auctions-data-center/purchase-orders");
    await expect(page.locator("h1")).toHaveText("Purchase Orders");
  });

  test("bidder: forbidden", async ({ page }) => {
    await loginAs(page, "bidder@buyerco.com", "Bidder123!");
    const res = await page.goto("/admin/auctions-data-center/purchase-orders");
    expect([403, 404]).toContain(res?.status() ?? 200);
  });
});
