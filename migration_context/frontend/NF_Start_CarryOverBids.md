# Nanoflow: NF_Start_CarryOverBids

**Allowed Roles:** EcoATM_BidData.User

## 📥 Inputs

- **$BidRound** (AuctionUI.BidRound)
- **$BidderRouterHelper** (AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (EcoATM_MDM.Week)
- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

## ⚙️ Execution Flow

1. **Create Variable **$TimerName** = `'CarryOverBids'`**
2. **Create Variable **$Description** = `'CarryOver for Buyer Code : '+$NP_BuyerCodeSelect_Helper/Code`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
5. **DB Retrieve **EcoATM_BuyerManagement.BuyerCodeSelect_Helper**  (Result: **$BuyerCodeSelect_HelperList**)**
6. **Delete **$BuyerCodeSelect_HelperList** from Database**
7. **Call Microflow **EcoATM_BidData.SUB_ProcessLastWeekBidData** (Result: **$LastAuctionBids**)**
8. **Close current page/popup**
9. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
10. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`**
11. **Open Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_HOT****
12. **Open Page: **EcoATM_BidData.PG_BidCarryover_Successful****
13. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
15. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
