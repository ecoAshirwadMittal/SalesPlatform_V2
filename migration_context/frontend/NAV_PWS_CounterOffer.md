# Nanoflow: NAV_PWS_CounterOffer

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Inprogress**)**
2. **Call Nanoflow **EcoATM_Direct_Theme.DS_GetBuyerCode_SessionAndTabHelper** (Result: **$BuyerCode_SessionAndTabHelper**)**
3. **Call Microflow **EcoATM_PWS.NAV_PWSCounterOffers****
4. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
