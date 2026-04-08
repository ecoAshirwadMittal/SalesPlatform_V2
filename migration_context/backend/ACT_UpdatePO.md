# Microflow Detailed Specification: ACT_UpdatePO

### 📥 Inputs (Parameters)
- **$POHelper** (Type: EcoATM_PO.POHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$POHelper**
      - Set **ENUM_ActionType** = `EcoATM_PO.ENUM_POActionType.Update`
      - Set **MissingBuyerCodeValidation** = `false`
      - Set **InvalidFileValidation** = `false`
      - Set **InValidPOPeriod** = `false`**
2. **Retrieve related **POHelper_PurchaseOrder** via Association from **$POHelper** (Result: **$PurchaseOrder**)**
3. 🔀 **DECISION:** `$PurchaseOrder != empty`
   ➔ **If [true]:**
      1. **Maps to Page: **EcoATM_PO.Create_PO_Week_Prompt****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Please select Purchase Order to Update`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.