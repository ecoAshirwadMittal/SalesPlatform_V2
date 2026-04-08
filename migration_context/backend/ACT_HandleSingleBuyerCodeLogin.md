# Microflow Detailed Specification: ACT_HandleSingleBuyerCodeLogin

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$SchedulingAuction = empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer[EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser/Name = $currentUser/Name]]` (Result: **$BuyerCodeList**)**
      2. **AggregateList**
      3. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$SchedulingAuction** (Result: **$NP_BuyerCodeSelect_HelperList**)**
      4. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser/Name= $currentUser/Name` (Result: **$Filter_BuyerCodesCurrentUser**)**
      5. **AggregateList**
      6. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$AccountList**)**
      7. 🔀 **DECISION:** `$CountNP_BuyerCodeSelect= 1`
         ➔ **If [true]:**
            1. **Maps to Page: **AuctionUI.Error_Auction_Not_Found****
            2. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.