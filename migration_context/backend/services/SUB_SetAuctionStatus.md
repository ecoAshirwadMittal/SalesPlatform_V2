# Microflow Detailed Specification: SUB_SetAuctionStatus

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList_2**)**
2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Started or $currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Closed or $currentObject/RoundStatus = AuctionUI.enum_SchedulingAuctionStatus.Scheduled` (Result: **$SchedulingAuctionList**)**
3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/RoundStatus!=AuctionUI.enum_SchedulingAuctionStatus.Closed` (Result: **$NewSchedulingAuctionList_notClosed**)**
4. 🔀 **DECISION:** `$NewSchedulingAuctionList_notClosed=empty`
   ➔ **If [true]:**
      1. **Update **$Auction**
      - Set **AuctionStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Closed`**
      2. **Commit/Save **$Auction** to Database**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/RoundStatus=AuctionUI.enum_SchedulingAuctionStatus.Started` (Result: **$NewSchedulingAuctionList_started**)**
      2. 🔀 **DECISION:** `$NewSchedulingAuctionList_started!=empty`
         ➔ **If [true]:**
            1. **Update **$Auction**
      - Set **AuctionStatus** = `AuctionUI.enum_SchedulingAuctionStatus.Started`**
            2. **Commit/Save **$Auction** to Database**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.