# Microflow Detailed Specification: ACT_OpenBidSubmitConfirmation

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$AuctionTimerHelper** (Type: AuctionUI.AuctionTimerHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound**)**
3. **Call Microflow **AuctionUI.ACT_BidSubmit_check_for_bids** (Result: **$HasBids**)**
4. 🔀 **DECISION:** `$HasBids = true`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$BidRound != empty`
         ➔ **If [true]:**
            1. **Retrieve related **BidRound_SchedulingAuction** via Association from **$BidRound** (Result: **$SchedulingAuction**)**
            2. 🔀 **DECISION:** `$SchedulingAuction/Round=3`
               ➔ **If [true]:**
                  1. **Update **$NP_BuyerCodeSelect_Helper**
      - Set **isUpsellRound** = `true`**
                  2. **Maps to Page: **AuctionUI.BidsSubmittedConfirmation****
                  3. **LogMessage**
                  4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name = $currentUser/Name]` (Result: **$EcoATMDirectUserList**)**
                  2. 🔀 **DECISION:** `$EcoATMDirectUserList/IsLocalUser = false or $EcoATMDirectUserList = empty`
                     ➔ **If [false]:**
                        1. **Call Microflow **AuctionUI.ACT_SubmitBidData****
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Maps to Page: **AuctionUI.BidsSubmittedConfirmation****
                        2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Show Message (Information): `No bids to submit.`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Maps to Page: **AuctionUI.BidsSubmittedConfirmation_NoBids****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.