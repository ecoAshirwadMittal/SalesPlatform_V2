# Nanoflow: BidCarryOver_Main

**Allowed Roles:** EcoATM_BidData.User

## 📥 Inputs

- **$BidUploadPageHelper** (AuctionUI.BidUploadPageHelper)
- **$BidRound** (AuctionUI.BidRound)
- **$BidderRouterHelper** (AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (EcoATM_MDM.Week)
- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

## ⚙️ Execution Flow

1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_BuyerManagement.BuyerCodeSelect_Helper**  (Result: **$BuyerCodeSelect_HelperList**)**
3. **Delete **$BuyerCodeSelect_HelperList** from Database**
4. **Retrieve related **NP_BuyerCodeSelect_Helper_SchedulingAuction** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$SchedulingAuction**)**
5. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `15`
      - Set **ProcessingMessage** = `'Started'`
      - Set **Status** = `EcoATM_BidData.enum_BidUploadStatus._New`**
6. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `70`
      - Set **ProcessingMessage** = `'70%'`**
7. **Call Microflow **EcoATM_BidData.SUB_CheckLastWeekBidDataExists** (Result: **$BidDataExists**)**
8. 🔀 **DECISION:** `$BidDataExists!=empty`
   ➔ **If [true]:**
      1. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `85`
      - Set **ProcessingMessage** = `'85%'`**
      2. **LogMessage (Warning)**
      3. **Call Microflow **EcoATM_BidData.SUB_ProcessLastWeekBids** (Result: **$LastAuctionBids**)**
      4. **LogMessage (Warning)**
      5. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `100`
      - Set **ProcessingMessage** = `'100%'`**
      6. **Close current page/popup**
      7. **LogMessage (Warning)**
      8. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
      9. **LogMessage (Warning)**
      10. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`**
      11. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
      12. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
      13. **Open Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_HOT****
      14. **Open Page: **EcoATM_BidData.PG_BidCarryover_Successful****
      15. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      16. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `85`
      - Set **ProcessingMessage** = `'85%'`**
      2. **LogMessage (Warning)**
      3. **LogMessage (Warning)**
      4. **Update **$BidUploadPageHelper** (and Save to DB)
      - Set **ProgressValue** = `100`
      - Set **ProcessingMessage** = `'100%'`**
      5. **Close current page/popup**
      6. **LogMessage (Warning)**
      7. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
      8. **LogMessage (Warning)**
      9. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`**
      10. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
      11. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`**
      12. **Open Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_HOT****
      13. **Open Page: **EcoATM_BidData.PG_BidCarryover_Successful****
      14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      15. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
