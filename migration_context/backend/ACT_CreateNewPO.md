# Microflow Detailed Specification: ACT_CreateNewPO

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PO.SUB_GetOrCreatePOHelper** (Result: **$POHelper**)**
2. **Create **EcoATM_PO.PurchaseOrder** (Result: **$NewPurchaseOrder**)**
3. **Update **$POHelper**
      - Set **ENUM_ActionType** = `EcoATM_PO.ENUM_POActionType._New`
      - Set **MissingBuyerCodeValidation** = `false`
      - Set **InvalidFileValidation** = `false`
      - Set **InValidPOPeriod** = `false`
      - Set **POHelper_PurchaseOrder** = `$NewPurchaseOrder`**
4. **Update **$NewPurchaseOrder**
      - Set **PurchaseOrder_PurchaseOrderDoc** = `$NewPurchaseOrder/EcoATM_PO.PurchaseOrder_PurchaseOrderDoc`**
5. **Maps to Page: **EcoATM_PO.Create_PO_Week_Prompt****
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.