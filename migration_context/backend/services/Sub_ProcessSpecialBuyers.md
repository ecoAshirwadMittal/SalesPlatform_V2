# Microflow Detailed Specification: Sub_ProcessSpecialBuyers

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_BuyerManagement.Buyer** Filter: `[isSpecialBuyer=true]` (Result: **$BuyerList**)**
2. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
   │ 1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
   │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe or $currentObject/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale` (Result: **$BuyerCodeList_WHandDW**)**
   │ 3. **List Operation: **Head** on **$undefined** (Result: **$TestBuyerCode**)**
   │ 4. 🔀 **DECISION:** `$TestBuyerCode != empty`
   │    ➔ **If [true]:**
   │       1. **Call Microflow **EcoATM_BuyerManagement.SUB_IsSpecialTreatmentBuyer** (Result: **$isSpecialTreatmentBuyer**)**
   │       2. 🔀 **DECISION:** `$isSpecialTreatmentBuyer = true`
   │          ➔ **If [false]:**
   │             1. **Call Microflow **Custom_Logging.SUB_Log_Info****
   │          ➔ **If [true]:**
   │             1. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList_WHandDW**
   │                │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_CreateBidDataForAllAE** (Result: **$BidDataList_Special**)**
   │                └─ **End Loop**
   │    ➔ **If [false]:**
   └─ **End Loop**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.