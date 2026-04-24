import { test, expect } from "@playwright/test";
import { isBackendAvailable } from "./_helpers/backend";

test.describe("Reserve Bids admin", () => {
  test.beforeAll(async () => {
    test.skip(!(await isBackendAvailable()), "requires Spring Boot backend on :8080");
  });

  test.beforeEach(async ({ page }) => {
    await page.goto("/login");
    await page.fill('[name="email"]', "admin@test.com");
    await page.fill('[name="password"]', "Admin123!");
    await page.click('button[type="submit"]');
    await page.waitForURL((u) => !u.pathname.includes("/login"));
  });

  test("overview loads and lists rows", async ({ page }) => {
    await page.goto("/admin/auctions-data-center/reserve-bids");
    await expect(page.locator("h1")).toContainText("Reserve Bids");
    await expect(page.locator("table tbody tr").first()).toBeVisible();
  });

  test("edit + audit flow", async ({ page }) => {
    await page.goto("/admin/auctions-data-center/reserve-bids");
    const firstEdit = page.locator("table tbody tr").first().getByText("Edit");
    await firstEdit.click();
    await page.waitForURL(/\/reserve-bids\/\d+$/);
    const bidInput = page.locator('input[type="number"]');
    const old = await bidInput.inputValue();
    await bidInput.fill((Number(old) + 1).toString());
    await page.getByText("Save").click();
    await page.waitForURL("**/reserve-bids");
  });
});
