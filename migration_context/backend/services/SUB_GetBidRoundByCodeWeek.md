# Microflow Detailed Specification: SUB_GetBidRoundByCodeWeek

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCode** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BuyerCode**)**
2. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
3. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound_BuyerCode**)**
4. **DB Retrieve **AuctionUI.BidRound** Filter: `[AuctionUI.BidRound_BuyerCode=$BuyerCode and AuctionUI.BidRound_SchedulingAuction=$SchedulingAuction]` (Result: **$BidRound**)**
5. 🔀 **DECISION:** `$BidRound!=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$BidRound`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_BuyerManagement.QualifiedBuyerCodes** Filter: `[ ( EcoATM_BuyerManagement.QualifiedBuyerCodes_BuyerCode = $BuyerCode and EcoATM_BuyerManagement.QualifiedBuyerCodes_SchedulingAuction = $SchedulingAuction ) ]` (Result: **$QualifiedBuyerCodes**)**
      2. 🔀 **DECISION:** `$QualifiedBuyerCodes != empty and $QualifiedBuyerCodes/Included = true`
         ➔ **If [true]:**
            1. **Update **$QualifiedBuyerCodes** (and Save to DB)
      - Set **OpenedDashboard** = `true`
      - Set **OpenedDashboardDateTime** = `[%CurrentDateTime%]`**
            2. **Create **AuctionUI.BidRound** (Result: **$NewBidRound**)
      - Set **Submitted** = `false`
      - Set **BidRound_SchedulingAuction** = `$SchedulingAuction`
      - Set **BidRound_BuyerCode** = `$BuyerCode`
      - Set **QualifiedBuyerCodes_BidRound** = `$QualifiedBuyerCodes`**
            3. 🏁 **END:** Return `$NewBidRound`
         ➔ **If [false]:**
            1. **Create **AuctionUI.BidRound** (Result: **$NewBidRound**)
      - Set **Submitted** = `false`
      - Set **BidRound_SchedulingAuction** = `$SchedulingAuction`
      - Set **BidRound_BuyerCode** = `$BuyerCode`
      - Set **QualifiedBuyerCodes_BidRound** = `$QualifiedBuyerCodes`**
            2. 🏁 **END:** Return `$NewBidRound`

**Final Result:** This process concludes by returning a [Object] value.