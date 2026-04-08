# Nanoflow: NF_Start_CarryOverBids_JA

**Allowed Roles:** EcoATM_BidData.User

## 📥 Inputs

- **$BidRound** (AuctionUI.BidRound)
- **$BidderRouterHelper** (AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (EcoATM_MDM.Week)
- **$Parent_NPBuyerCodeSelectHelper** (EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$Current_Week** (EcoATM_MDM.Week)

## ⚙️ Execution Flow

1. **Create Variable **$TimerName** = `'CarryOverBids'`**
2. **Create Variable **$Description** = `'CarryOver for Buyer Code : '+$NP_BuyerCodeSelect_Helper/Code`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
5. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
6. **DB Retrieve **EcoATM_BuyerManagement.BuyerCodeSelect_Helper**  (Result: **$BuyerCodeSelect_HelperList**)**
7. **Delete **$BuyerCodeSelect_HelperList** from Database**
8. **Create **AuctionUI.CarryOverBidsNP** (Result: **$NewCarryOverBidsNP**)
      - Set **FromWeek** = `$Last_Week/WeekNumber`
      - Set **FromBuyerCode** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **ToWeek** = `$Current_Week/WeekNumber`
      - Set **ToYear** = `$Current_Week/Year`
      - Set **FromYear** = `$Last_Week/Year`
      - Set **ToBuyerCode** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **OverwriteExisting** = `true`
      - Set **DryRun** = `false`
      - Set **BatchSize** = `1000`**
9. **Call Microflow **AuctionUI.ACT_CarryForwardBids****
10. **Close current page/popup**
11. **Call Microflow **AuctionUI.SUB_AuctionTimerHelper** (Result: **$AuctionTimerHelper**)**
12. **Create **EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
13. **Create **EcoATM_BuyerManagement.BuyerCodeSelect_Helper** (Result: **$NewBuyerCodeSelect_Helper**)
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`**
14. **Create **EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`**
15. **Open Page: **EcoATM_BuyerManagement.PG_Bidder_Dashboard_DG2****
16. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
17. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
18. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
