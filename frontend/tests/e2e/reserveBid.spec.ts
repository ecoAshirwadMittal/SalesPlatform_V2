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

  test("toolbar matches legacy — [Download] [Upload EB Price], no New", async ({ page }) => {
    // RBL-P4: EB is authored via Excel upload only. The toolbar is exactly
    // Download + Upload EB Price; the legacy /new manual-create route is gone.
    await page.goto("/admin/auctions-data-center/reserve-bids");
    await expect(page.getByRole("button", { name: "Download" })).toBeVisible();
    await expect(page.getByRole("button", { name: "Upload EB Price" })).toBeVisible();
    await expect(page.getByRole("button", { name: "New", exact: true })).toHaveCount(0);
    // The removed route 404s rather than rendering a create form.
    await page.goto("/admin/auctions-data-center/reserve-bids/new");
    await expect(page.getByText("New Reserve Bid")).toHaveCount(0);
  });

  test("per-row audit eye opens the audit modal", async ({ page }) => {
    // RBL-P3: the only per-row affordance is a single eye that opens the audit
    // view as a modal (no Edit / Audit / Delete text links).
    await page.goto("/admin/auctions-data-center/reserve-bids");
    await expect(page.locator("table tbody tr").first()).toBeVisible();
    await expect(page.getByRole("link", { name: "Edit" })).toHaveCount(0);
    const firstAuditEye = page
      .getByRole("button", { name: /View audit history for product/ })
      .first();
    await firstAuditEye.click();
    const dialog = page.getByRole("dialog");
    await expect(dialog).toBeVisible();
    await expect(dialog).toContainText("Audit");
  });
});
