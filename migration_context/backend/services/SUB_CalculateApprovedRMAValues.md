# Microflow Detailed Specification: SUB_CalculateApprovedRMAValues

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **RMAItem_RMA** via Association from **$RMA** (Result: **$RMAItemList**)**
2. **List Operation: **Filter** on **$undefined** where `EcoATM_RMA.ENUM_RMAItemStatus.Approve` (Result: **$RMAItemList_Approved**)**
3. **AggregateList**
4. 🔀 **DECISION:** `$RMAItemList_Approved!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_RMA.SUB_CalculateRMAApprovedSummary****
      2. 🏁 **END:** Return `'Approved'`
   ➔ **If [false]:**
      1. **List Operation: **Filter** on **$undefined** where `EcoATM_RMA.ENUM_RMAItemStatus.Decline` (Result: **$RMAItemList_Declined**)**
      2. **AggregateList**
      3. 🔀 **DECISION:** `$Count_Declined = $Count_Total`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `empty`
         ➔ **If [true]:**
            1. **Update **$RMA**
      - Set **ApprovedSKUs** = `0`
      - Set **ApprovedQty** = `0`
      - Set **ApprovedSalesTotal** = `0`**
            2. 🏁 **END:** Return `'Declined'`

**Final Result:** This process concludes by returning a [String] value.