# Microflow Detailed Specification: ACT_SubmitBidData

### 📥 Inputs (Parameters)
- **$BuyerCodeSelect_Helper_NP** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$AuctionTimerHelper** (Type: AuctionUI.AuctionTimerHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[Name = $currentUser/Name]` (Result: **$EcoATMDirectUserList**)**
3. **Create Variable **$isBuyerUser** = `true`**
4. 🔀 **DECISION:** `$EcoATMDirectUserList/IsLocalUser = false or $EcoATMDirectUserList = empty`
   ➔ **If [true]:**
      1. **Update Variable **$isBuyerUser** = `false`**
      2. **Close current page/popup**
      3. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
      4. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$BuyerCodeSelect_Helper_NP** (Result: **$BidRound**)**
      5. **Call Microflow **AuctionUI.ACT_CreateBidSubmitLog** (Result: **$BidSubmitLog**)**
      6. **Retrieve related **BidRound_BuyerCode** via Association from **$BidRound** (Result: **$BuyerCode**)**
      7. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound]` (Result: **$BidDataList**)**
      8. **List Operation: **FilterByExpression** on **$undefined** where `($currentObject/BidAmount != empty and $currentObject/BidAmount>0) or ($currentObject/SubmittedBidAmount != empty and $currentObject/SubmittedBidAmount>0)` (Result: **$ExcludeEmptyBidDataList**)**
      9. **List Operation: **FilterByExpression** on **$undefined** where `($currentObject/BidAmount != empty and $currentObject/BidAmount < $BuyerCodeSubmitConfig/MinimumAllowedBid) and ($currentObject/BidAmount != empty and $currentObject/BidAmount > 0)` (Result: **$BidDataList_BelowMinBid_nonzero**)**
      10. **AggregateList**
      11. **CreateList**
      12. 🔄 **LOOP:** For each **$IteratorBidData** in **$ExcludeEmptyBidDataList**
         │ 1. **Update **$IteratorBidData**
      - Set **SubmittedBidAmount** = `$IteratorBidData/BidAmount`
      - Set **SubmittedBidQuantity** = `$IteratorBidData/BidQuantity`
      - Set **SubmittedDateTime** = `[%CurrentDateTime%]`**
         │ 2. **Add **$$IteratorBidData
** to/from list **$BidDataToCommit****
         └─ **End Loop**
      13. **Commit/Save **$BidDataToCommit** to Database**
      14. **Update **$BidRound** (and Save to DB)
      - Set **Submitted** = `true`
      - Set **Note** = `$BuyerCodeSelect_Helper_NP/Note`
      - Set **BidRound_SchedulingAuction** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_SchedulingAuction`
      - Set **BidRound_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`
      - Set **SubmittedDatatime** = `[%CurrentDateTime%]`**
      15. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/CreateExcelBidExport = true`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_BuyerManagement.ACT_BidDataDoc_PopulateExcelDoc****
            2. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendFilesToSharepointOnSubmit = true`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint****
                  2. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **Code** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
                  3. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
                  4. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        4. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        5. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        3. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **Code** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
                  2. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
                  3. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        4. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        5. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        3. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendFilesToSharepointOnSubmit = true`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint****
                  2. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **Code** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
                  3. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
                  4. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        4. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        5. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        3. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **Code** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
                  2. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
                  3. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        4. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        5. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        3. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$isBuyerUser** = `true`**
      2. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
      3. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$BuyerCodeSelect_Helper_NP** (Result: **$BidRound**)**
      4. **Call Microflow **AuctionUI.ACT_CreateBidSubmitLog** (Result: **$BidSubmitLog**)**
      5. **Retrieve related **BidRound_BuyerCode** via Association from **$BidRound** (Result: **$BuyerCode**)**
      6. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound]` (Result: **$BidDataList**)**
      7. **List Operation: **FilterByExpression** on **$undefined** where `($currentObject/BidAmount != empty and $currentObject/BidAmount>0) or ($currentObject/SubmittedBidAmount != empty and $currentObject/SubmittedBidAmount>0)` (Result: **$ExcludeEmptyBidDataList**)**
      8. **List Operation: **FilterByExpression** on **$undefined** where `($currentObject/BidAmount != empty and $currentObject/BidAmount < $BuyerCodeSubmitConfig/MinimumAllowedBid) and ($currentObject/BidAmount != empty and $currentObject/BidAmount > 0)` (Result: **$BidDataList_BelowMinBid_nonzero**)**
      9. **AggregateList**
      10. **CreateList**
      11. 🔄 **LOOP:** For each **$IteratorBidData** in **$ExcludeEmptyBidDataList**
         │ 1. **Update **$IteratorBidData**
      - Set **SubmittedBidAmount** = `$IteratorBidData/BidAmount`
      - Set **SubmittedBidQuantity** = `$IteratorBidData/BidQuantity`
      - Set **SubmittedDateTime** = `[%CurrentDateTime%]`**
         │ 2. **Add **$$IteratorBidData
** to/from list **$BidDataToCommit****
         └─ **End Loop**
      12. **Commit/Save **$BidDataToCommit** to Database**
      13. **Update **$BidRound** (and Save to DB)
      - Set **Submitted** = `true`
      - Set **Note** = `$BuyerCodeSelect_Helper_NP/Note`
      - Set **BidRound_SchedulingAuction** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_SchedulingAuction`
      - Set **BidRound_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`
      - Set **SubmittedDatatime** = `[%CurrentDateTime%]`**
      14. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/CreateExcelBidExport = true`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_BuyerManagement.ACT_BidDataDoc_PopulateExcelDoc****
            2. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendFilesToSharepointOnSubmit = true`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint****
                  2. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **Code** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
                  3. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
                  4. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        4. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        5. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        3. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **Code** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
                  2. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
                  3. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        4. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        5. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        3. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendFilesToSharepointOnSubmit = true`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint****
                  2. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **Code** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
                  3. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
                  4. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        4. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        5. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        3. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **Code** = `$BuyerCodeSelect_Helper_NP/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$BuyerCodeSelect_Helper_NP/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
                  2. **Call Microflow **AuctionUI.SUB_SendSubmitBidConfirmationEmail****
                  3. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/SendBidDataToSnowflake`
                     ➔ **If [true]:**
                        1. **Call Microflow **AuctionUI.SUB_SendBidDataToSnowflake****
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        4. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        5. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. **Create **EcoATM_BuyerManagement.BidSubmitConfirmationHelper** (Result: **$BidSubmitConfirmationHelper**)
      - Set **BidsBelowMinimumBid** = `$BidsBelowMinBidConfig`
      - Set **MinBidAmount** = `$BuyerCodeSubmitConfig/MinimumAllowedBid`**
                        3. **Maps to Page: **AuctionUI.BidsSubmitted_ReturnToBidPage****
                        4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.