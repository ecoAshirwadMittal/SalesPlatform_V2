# Microflow Detailed Specification: SUB_ListBuyerCodesForSpecialBuyers_WholeSale_Datawipe

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_BuyerManagement.Buyer** Filter: `[isSpecialBuyer=true]` (Result: **$BuyerList**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
   │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
   │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe or $currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale` (Result: **$BuyerCode_DW_WH**)**
   │ 3. 🔀 **DECISION:** `$BuyerCode_DW_WH != empty`
   │    ➔ **If [true]:**
   │       1. **Create Variable **$IsSpecialBuyerEligible** = `true`**
   │       2. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCode_DW_WH**
   │          │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_IsSpecialTreatmentBuyer** (Result: **$isSpecialTreatment**)**
   │          │ 2. **Update Variable **$IsSpecialBuyerEligible** = `$isSpecialTreatment and $IsSpecialBuyerEligible`**
   │          └─ **End Loop**
   │       3. 🔀 **DECISION:** `$IsSpecialBuyerEligible`
   │          ➔ **If [false]:**
   │          ➔ **If [true]:**
   │             1. **Add **$$BuyerCode_DW_WH
** to/from list **$SpecialBuyerCodeList****
   │    ➔ **If [false]:**
   └─ **End Loop**
4. 🏁 **END:** Return `$SpecialBuyerCodeList`

**Final Result:** This process concludes by returning a [List] value.