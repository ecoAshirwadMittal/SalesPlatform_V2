# Microflow Detailed Specification: SUB_BidDataImport_Round3BidRank

### 📥 Inputs (Parameters)
- **$ImportFile** (Type: Custom_Excel_Import.ImportFile)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$BidUploadPageHelper** (Type: AuctionUI.BidUploadPageHelper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
2. **Call Microflow **EcoATM_BidData.SUB_BidDataImport_GetValidBidData_Round3BidRank** (Result: **$ExcelImport_BidData**)**
3. 🔀 **DECISION:** `$ExcelImport_BidData != empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_BidData.SUB_BidData_Transform_Round3BidRank** (Result: **$BidData_ToCOmmit**)**
      2. 🔀 **DECISION:** `$BidData_ToCOmmit != empty`
         ➔ **If [true]:**
            1. **Close current page/popup**
            2. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
            3. **Commit/Save **$BidData_ToCOmmit** to Database**
            4. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound and BidAmount > 0]` (Result: **$BidDataList**)**
            5. **AggregateList**
            6. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **RowCount** = `$BidCount`
      - Set **FileName** = `$ImportFile/Name`**
            7. **Delete**
            8. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BuyerCodeSelect_Helper_BuyerCode** = `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode`**
            9. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
            10. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
            11. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BidRound != empty`
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_Warning** (Result: **$Log**)**
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/LegacyAuctionDashboardActive = true`
                     ➔ **If [false]:**
                        1. **Maps to Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_DG2****
                        2. **Maps to Page: **EcoATM_BidData.BidImportComplete****
                        3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        4. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Maps to Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_HOT****
                        2. **Maps to Page: **EcoATM_BidData.BidImportComplete****
                        3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$SchedulingAuction/Round = 2`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$SchedulingAuction/Round = 3`
                     ➔ **If [false]:**
                        1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Bids or Quantity were lower than existing values'`**
                        2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        3. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Bids or Quantity were lower than existing values'`**
                  2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'There were no records to process'`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.