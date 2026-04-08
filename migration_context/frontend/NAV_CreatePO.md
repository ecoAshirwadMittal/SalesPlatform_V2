# Microflow Detailed Specification: NAV_CreatePO

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PO.SUB_GetOrCreatePOHelper** (Result: **$POHelper**)**
2. **DB Retrieve **EcoATM_PO.PurchaseOrder**  (Result: **$PurchaseOrder**)**
3. **Update **$POHelper**
      - Set **POHelper_PurchaseOrder** = `$PurchaseOrder`**
4. **Maps to Page: **EcoATM_PO.PurchaseOrder_Main****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.