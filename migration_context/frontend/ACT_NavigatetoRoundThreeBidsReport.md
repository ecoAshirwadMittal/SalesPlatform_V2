# Nanoflow: ACT_NavigatetoRoundThreeBidsReport

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesLeader, AuctionUI.SalesOps, AuctionUI.SalesRep

## ⚙️ Execution Flow

1. **Call Microflow **AuctionUI.DS_GetOrCreateAggregatedInventoryHelper** (Result: **$AggInventoryHelper**)**
2. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekEndDateTime > '[%CurrentDateTime%]']` (Result: **$Week**)**
3. **Update **$AggInventoryHelper**
      - Set **AggInventoryHelper_Week** = `$Week`**
4. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
5. **Open Page: **AuctionUI.PG_RoundThreeBidsReportByBuyer****
6. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
