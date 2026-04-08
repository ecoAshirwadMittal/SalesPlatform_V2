# Microflow Detailed Specification: ACT_OpenBidderDashboard

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
2. **DB Retrieve **EcoATM_BuyerManagement.BuyerCodeSelect_Helper**  (Result: **$BuyerCodeSelect_HelperList**)**
3. **Delete**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. **DB Retrieve **AuctionUI.SchedulingAuction**  (Result: **$SchedulingAuctionList**)**
6. 🔀 **DECISION:** `$SchedulingAuctionList != empty`
   ➔ **If [false]:**
      1. **JavaCallAction**
      2. **Close current page/popup**
      3. **Maps to Page: **AuctionUI.Error_Auction_Not_Found****
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_GetMostRecentAuction** (Result: **$Auction**)**
      2. **Call Microflow **AuctionUI.ACT_GetActiveSchedulingAuction** (Result: **$SchedulingAuctionStartedRound**)**
      3. **Call Microflow **EcoATM_BuyerManagement.ACT_GetSubmittedBidRounds** (Result: **$BidRouterHelper**)**
      4. **Call Microflow **AuctionUI.SUB_CheckRoundIncluded** (Result: **$isBuyerCodeIncluded**)**
      5. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
      6. 🔀 **DECISION:** `$isBuyerCodeIncluded = true`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$BidRouterHelper/Round2BidSubmitted=false and $BidRouterHelper/Round2Status='Closed' and $BidRouterHelper/R2isActive and $SchedulingAuctionStartedRound/Round!=3`
               ➔ **If [true]:**
                  1. **Close current page/popup**
                  2. **Maps to Page: **EcoATM_BuyerManagement.BidDownloadOnBuyerCodeSelect****
                  3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$SchedulingAuctionStartedRound!=empty`
                     ➔ **If [false]:**
                        1. **Update **$BidRouterHelper**
      - Set **Round1Status** = `'AllRoundsDone'`**
                        2. **Close current page/popup**
                        3. **Maps to Page: **EcoATM_BuyerManagement.BidDownloadOnBuyerCodeSelect****
                        4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        5. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **DB Retrieve **System.UserRole** Filter: `[System.UserRoles = $currentUser and Name='SalesRep']` (Result: **$SalesRepUserRole**)**
                        2. 🔀 **DECISION:** `$SalesRepUserRole!=empty`
                           ➔ **If [false]:**
                              1. 🔀 **DECISION:** `$SchedulingAuctionStartedRound/Round!=3`
                                 ➔ **If [false]:**
                                    1. **Update **$BidRouterHelper**
      - Set **Round1Status** = `'AllRoundsDone'`**
                                    2. **Update **$BidRouterHelper**
      - Set **Round1Status** = `'AllRoundsDone'`
      - Set **CurrentRound** = `if $BidRouterHelper/Round2Status='Closed' and $BidRouterHelper/R2isActive=true then '2' else '1'`**
                                    3. **Close current page/popup**
                                    4. **Maps to Page: **EcoATM_BuyerManagement.BidDownloadOnBuyerCodeSelect****
                                    5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                    6. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. **Update **$NP_BuyerCodeSelect_Helper** (and Save to DB)
      - Set **NP_BuyerCodeSelect_Helper_SchedulingAuction** = `$SchedulingAuctionStartedRound`**
                                    2. **Call Microflow **AuctionUI.GetCurrentBidRoundBuyerCodeSelect****
                                    3. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
                                    4. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
                                    5. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper_Gallery**)**
                                    6. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper_Gallery`**
                                    7. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound**)**
                                    8. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BidRound != empty`
                                       ➔ **If [false]:**
                                          1. **Call Microflow **Custom_Logging.SUB_Log_Warning** (Result: **$Log**)**
                                          2. **Show Message (Error): `Bidround is empty. ACT_OpenBidderDashboard`**
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [true]:**
                                          1. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/LegacyAuctionDashboardActive = true`
                                             ➔ **If [true]:**
                                                1. **Maps to Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_HOT****
                                                2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                3. 🏁 **END:** Return empty
                                             ➔ **If [false]:**
                                                1. **Call Microflow **AuctionUI.ACT_CreateBidData** (Result: **$BidDataList**)**
                                                2. **Maps to Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_DG2****
                                                3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                4. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Update **$NP_BuyerCodeSelect_Helper** (and Save to DB)
      - Set **NP_BuyerCodeSelect_Helper_SchedulingAuction** = `$SchedulingAuctionStartedRound`**
                              2. **Call Microflow **AuctionUI.GetCurrentBidRoundBuyerCodeSelect****
                              3. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
                              4. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
                              5. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper_Gallery**)**
                              6. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper_Gallery`**
                              7. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound**)**
                              8. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BidRound != empty`
                                 ➔ **If [false]:**
                                    1. **Call Microflow **Custom_Logging.SUB_Log_Warning** (Result: **$Log**)**
                                    2. **Show Message (Error): `Bidround is empty. ACT_OpenBidderDashboard`**
                                    3. 🏁 **END:** Return empty
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/LegacyAuctionDashboardActive = true`
                                       ➔ **If [true]:**
                                          1. **Maps to Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_HOT****
                                          2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          3. 🏁 **END:** Return empty
                                       ➔ **If [false]:**
                                          1. **Call Microflow **AuctionUI.ACT_CreateBidData** (Result: **$BidDataList**)**
                                          2. **Maps to Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_DG2****
                                          3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Close current page/popup**
            2. **Maps to Page: **EcoATM_BuyerManagement.BidDownloadOnBuyerCodeSelect****
            3. **Call Microflow **Custom_Logging.SUB_Log_Info****
            4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.