# Microflow Detailed Specification: SUB_Round3_PreProcessRoundData

### 📥 Inputs (Parameters)
- **$Round2SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.SUB_Round2_DeleteUnsubmittedBids****
2. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction=$Round2SchedulingAuction/AuctionUI.SchedulingAuction_Auction] [Round = 3]` (Result: **$Round3SchedulingAuction**)**
3. 🔀 **DECISION:** `$Round3SchedulingAuction != empty and $Round3SchedulingAuction/HasRound`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_InitializeRound3****
      2. **Call Microflow **AuctionUI.SUB_GenerateRound3QualifiedBuyerCodes** (Result: **$BuyerCodeList**)**
      3. **Call Microflow **AuctionUI.ACT_Generate_RoundThreeQualifiedBuyersReport** (Result: **$RoundThreeBuyersDataReportList**)**
      4. **Update **$Round3SchedulingAuction** (and Save to DB)
      - Set **SchedulingAuction_QualifiedBuyers** = `$BuyerCodeList`**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.