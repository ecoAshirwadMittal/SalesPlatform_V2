# Microflow Detailed Specification: ACT_Round2BuyerCodes_noXPATH

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SchedulingAuction_Auction** via Association from **$Auction** (Result: **$SchedulingAuctionList**)**
2. **List Operation: **Find** on **$undefined** where `1` (Result: **$SchedulingAuctionRound1**)**
3. **LogMessage**
4. **Retrieve related **BidRound_SchedulingAuction** via Association from **$SchedulingAuctionRound1** (Result: **$BidRoundList**)**
5. **CreateList**
6. 🔄 **LOOP:** For each **$IteratorBidRound** in **$BidRoundList**
   │ 1. **Retrieve related **BidData_BidRound** via Association from **$IteratorBidRound** (Result: **$BidDataList**)**
   │ 2. **List Operation: **FilterByExpression** on **$undefined** where `($currentObject/Merged_Grade = 'A_YYY' or $currentObject/Merged_Grade = 'C_YNY/G_YNN' or $currentObject/Merged_Grade = 'E_YYN') and (($currentObject/BidAmount div $currentObject/TargetPrice >= .85) or ($currentObject/TargetPrice - $currentObject/BidAmount <=15) )` (Result: **$BidDataListFiltered**)**
   │ 3. 🔀 **DECISION:** `$BidDataListFiltered != empty`
   │    ➔ **If [true]:**
   │       1. **LogMessage**
   │       2. **Add **$$IteratorBidRound/AuctionUI.BidRound_BuyerCode** to/from list **$EligibleBuyerCodeList****
   │    ➔ **If [false]:**
   │       1. **LogMessage**
   └─ **End Loop**
7. 🏁 **END:** Return `$EligibleBuyerCodeList`

**Final Result:** This process concludes by returning a [List] value.