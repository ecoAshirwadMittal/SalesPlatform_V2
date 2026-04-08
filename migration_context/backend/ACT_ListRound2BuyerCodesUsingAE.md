# Microflow Detailed Specification: ACT_ListRound2BuyerCodesUsingAE

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'ListRound2BuyersBasedOnBidData'`**
2. **Create Variable **$Description** = `'List Round 2 buyers with qualifying bids'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **DB Retrieve **AuctionUI.BidRoundSelectionFilter** Filter: `[ ( Round = 2 ) ]` (Result: **$BidRoundSelectionFilter**)**
5. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[ ( BuyerCodeType = 'Wholesale' or BuyerCodeType = 'Data_Wipe' ) and ( EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active' ) ]` (Result: **$BuyerCodeList**)**
6. **CreateList**
7. 🔀 **DECISION:** `$BidRoundSelectionFilter/RegularBuyerQualification != EcoATM_BuyerManagement.Enum_RegularBuyerQualification.All_Buyers`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
         │ 1. **Call Microflow **AuctionUI.SUB_Round2AggregatedInventorySingleItem** (Result: **$AggregatedInventoryList**)**
         └─ **End Loop**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$BuyerCodeList_Qualified`
   ➔ **If [false]:**
      1. **Add **$$BuyerCodeList** to/from list **$BuyerCodeList_Qualified****
      2. 🏁 **END:** Return `$BuyerCodeList_Qualified`

**Final Result:** This process concludes by returning a [List] value.