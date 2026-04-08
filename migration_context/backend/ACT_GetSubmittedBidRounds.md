# Microflow Detailed Specification: ACT_GetSubmittedBidRounds

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **AuctionUI.ACT_GetMostRecentAuction** (Result: **$Auction**)**
3. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
4. **List Operation: **Sort** on **$undefined** sorted by: Round (Ascending) (Result: **$SortedScheduledAuction**)**
5. **Create **AuctionUI.BidderRouterHelper** (Result: **$ObjectBidderRouter**)
      - Set **BidderRouterHelper_Week** = `$Auction/AuctionUI.Auction_Week`**
6. 🔄 **LOOP:** For each **$IteratorSchedulingAuction** in **$SortedScheduledAuction**
   │ 1. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidRound_SchedulingAuction = $IteratorSchedulingAuction] [AuctionUI.BidRound_BuyerCode=$BuyerCode]` (Result: **$CurrentBidRound**)**
   │ 2. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $CurrentBidRound]` (Result: **$BidRoundBidDataList**)**
   │ 3. **AggregateList**
   │ 4. **Call Microflow **EcoATM_BuyerManagement.SUB_IsSpecialTreatmentBuyer** (Result: **$isSpecialTreatmentBuyer**)**
   │ 5. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round=1`
   │    ➔ **If [true]:**
   │       1. **Update **$ObjectBidderRouter**
      - Set **Round1Status** = `toString($IteratorSchedulingAuction/RoundStatus)`
      - Set **Round1BidSubmitted** = `$CurrentBidRound!=empty and $CurrentBidRound/Submitted`
      - Set **CurrYearWeek** = `$IteratorSchedulingAuction/Auction_Week_Year`
      - Set **BidderRouterHelper_Week** = `$Auction/AuctionUI.Auction_Week`
      - Set **CurrentRound** = `if $IteratorSchedulingAuction/RoundStatus= AuctionUI.enum_SchedulingAuctionStatus.Started then 'Round 1' else $ObjectBidderRouter/CurrentRound`
      - Set **isSpecialTreatmentBuyer** = `$isSpecialTreatmentBuyer`
      - Set **Round1BidCount** = `$BidRoundBidCount`**
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round=2`
   │          ➔ **If [true]:**
   │             1. **Update **$ObjectBidderRouter**
      - Set **Round2Status** = `toString($IteratorSchedulingAuction/RoundStatus)`
      - Set **Round2BidSubmitted** = `$CurrentBidRound!=empty and $CurrentBidRound/Submitted`
      - Set **CurrentRound** = `if $IteratorSchedulingAuction/RoundStatus= AuctionUI.enum_SchedulingAuctionStatus.Started then 'Round 2' else if($ObjectBidderRouter!=empty) then $ObjectBidderRouter/CurrentRound else ''`
      - Set **R2isActive** = `$IteratorSchedulingAuction/RoundStatus= AuctionUI.enum_SchedulingAuctionStatus.Started`
      - Set **isSpecialTreatmentBuyer** = `$isSpecialTreatmentBuyer`
      - Set **Round2BidCount** = `$BidRoundBidCount`**
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$IteratorSchedulingAuction/Round=3`
   │                ➔ **If [true]:**
   │                   1. **Update **$ObjectBidderRouter**
      - Set **Round3Status** = `toString($IteratorSchedulingAuction/RoundStatus)`
      - Set **Round3BidSubmitted** = `$CurrentBidRound!=empty and $CurrentBidRound/Submitted`
      - Set **R3isActive** = `$IteratorSchedulingAuction/RoundStatus= AuctionUI.enum_SchedulingAuctionStatus.Started`
      - Set **CurrentRound** = `if $IteratorSchedulingAuction/RoundStatus= AuctionUI.enum_SchedulingAuctionStatus.Started then 'Upsell Round' else if($ObjectBidderRouter!=empty) then $ObjectBidderRouter/CurrentRound else ''`
      - Set **isSpecialTreatmentBuyer** = `$isSpecialTreatmentBuyer`
      - Set **Round3BidCount** = `$BidRoundBidCount`**
   │                ➔ **If [false]:**
   └─ **End Loop**
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return `$ObjectBidderRouter`

**Final Result:** This process concludes by returning a [Object] value.