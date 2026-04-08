# SalesPlatform — Test Cases

> **1** test cases with label `SalesPlatform`.

> Source: Jira project **SPKB** (issuetype = Test)

---

### SPKB-1596 — Validate Deposco API Admin Configuration & Inventory Sync

- **Status**: Done
- **Priority**: P2
- **Labels**: `7.0.0`, `PWS`, `SalesPlatform`
- **Linked Issues**: SPKB-1363

**Description:**
### Steps & Expected Results
- Navigate & Log In
- Go to https://buy-qa.ecoatmdirect.com/p/orderdashboard.
- Sign in with your Sales Platform credentials.
- Expect: You land on the Order Dashboard with the “Premium Wholesale” grid visible.
- Switch to Wholesale
- Click the Profile menu (top right) → Switch to Wholesale.
- Expect: The “Premium Wholesale” page is active, showing columns like SKU, Avl. Qty, etc.
- Open Deposco Config
- From the left nav, click Settings → PWS Control Center → Deposco tile.
- Expect: The Deposco Config screen appears, showing:
- Base URL = https://ecoatm-ua.deposco.com/
- Username / Password fields populated (password masked)
- Last Sync Time and Page Count filled in (per your screenshot)
- Capture Network Traffic
- Open DevTools → Network tab (or configure your proxy).
- Clear existing logs so you only see new calls.
- Load Inventory from Deposco
- Click the Load PWS inventory Deposco button.
- Expect:
- A GET request fires to
```
php-template
```
CopyEdit
https://ecoatm-ua.deposco.com/integration/ecoatm/inventory/full   ?startActivityTime=<timestamp>&pageNo=<n>
- The response is 200 OK with JSON containing your test SKU and fields: total, availableToPromise, unallocated.
- Validate Initial Sync
- Without touching anything in Deposco, verify the Sales Platform grid’s Avl. Qty, ATPQty, Reserved match what the full API returned.
- Expect: The numbers align exactly with the JSON payload.
- Make an Inventory Change in Deposco
- In the Deposco UI (ecoatm-ua.deposco.com), locate your test SKU.
- Change its On-Hand quantity (e.g., +1) or add a new SKU with a known quantity.
- Save/apply the change.
- Re-run the Load
- Back in PWS, click Load PWS inventory Deposco again.
- Expect:
- The same API endpoint is called (with a new startActivityTime).
- The returned JSON now reflects your updated quantity.
- The Sales Platform grid updates to show the new Avl. Qty, ATPQty, Reserved values.
- Cleanup
- Revert any test changes in Deposco (return your SKU to its original On-Hand).
- Optionally, clear or reset the PWS page count or last-sync timestamp to avoid interfering with other tests.
Pass Criteria:
- Config screen shows exactly the values in your screenshots.
- “Load PWS inventory” triggers the correct full-inventory API call.
- Initial and subsequent syncs accurately pull and display Deposco data in PWS.
- No errors appear in the UI or network logs.
