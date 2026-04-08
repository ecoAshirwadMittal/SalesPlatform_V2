# Microflow Detailed Specification: DS_GetOrCreatePODoc

### 📥 Inputs (Parameters)
- **$PurchaseOrder** (Type: EcoATM_PO.PurchaseOrder)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **PurchaseOrder_PurchaseOrderDoc** via Association from **$PurchaseOrder** (Result: **$PurchaseOrderDoc**)**
2. 🔀 **DECISION:** `$PurchaseOrderDoc != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$PurchaseOrderDoc`
   ➔ **If [false]:**
      1. **Create **EcoATM_PO.PurchaseOrderDoc** (Result: **$NewPurchaseOrderDoc**)**
      2. 🏁 **END:** Return `$NewPurchaseOrderDoc`

**Final Result:** This process concludes by returning a [Object] value.