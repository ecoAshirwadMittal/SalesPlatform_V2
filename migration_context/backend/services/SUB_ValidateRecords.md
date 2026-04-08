# Microflow Detailed Specification: SUB_ValidateRecords

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$RMAFile** (Type: EcoATM_RMA.RMAFile)
- **$RMARequest_ImportHelperList** (Type: EcoATM_RMA.RMARequest_ImportHelper)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_RMA.VAL_RMARequestFile** (Result: **$RMAItemList**)**
2. 🔀 **DECISION:** `$RMAFile/IsValid`
   ➔ **If [false]:**
      1. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$RMAItemList_InvalidReason**)**
      2. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$RMAItemList_InValidIMEI_List**)**
      3. **Update **$RMAFile** (and Save to DB)
      - Set **InvalidReason** = `length($RMAItemList_InValidIMEI_List) + ' items have invalid IMEIs or serial numbers'`**
      4. 🏁 **END:** Return `$RMAItemList_InValidIMEI_List`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$RMAItemList`

**Final Result:** This process concludes by returning a [List] value.