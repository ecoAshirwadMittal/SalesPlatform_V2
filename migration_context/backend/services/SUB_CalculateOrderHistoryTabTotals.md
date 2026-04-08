# Microflow Detailed Specification: SUB_CalculateOrderHistoryTabTotals

### 📥 Inputs (Parameters)
- **$OrderHistoryHelper** (Type: EcoATM_PWS.OrderHistoryHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CalcOrderHistoryTotals'`**
2. **Create Variable **$Description** = `'Calc Order History Tab Totals'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_PWS.OfferAndOrdersView**  (Result: **$OfferAndOrdersViewList_All**)**
5. **AggregateList**
6. **DB Retrieve **EcoATM_PWS.OfferAndOrdersView** Filter: `[ OrderStatus = 'In Process' or OrderStatus = 'Offer Pending' or OrderStatus = 'Awaiting Carrier Pickup' ]` (Result: **$OfferAndOrdersViewList_InProcess**)**
7. **AggregateList**
8. **DB Retrieve **EcoATM_PWS.OfferAndOrdersView** Filter: `[ OrderStatus!='In Process' and OrderStatus!='Offer Pending' and OrderStatus!='Awaiting Carrier Pickup' ]` (Result: **$OfferAndOrdersViewList_Complete**)**
9. **AggregateList**
10. **DB Retrieve **EcoATM_PWS.OfferAndOrdersView** Filter: `[LastUpdateDate >= $OrderHistoryHelper/RecentDateCutoff]` (Result: **$OfferAndOrdersViewList_Recent**)**
11. **AggregateList**
12. **Update **$OrderHistoryHelper**
      - Set **CountAll** = `$CountAll`
      - Set **CountComplete** = `$CountComplete`
      - Set **CountRecent** = `$CountRecent`
      - Set **CountInProcess** = `$CountInProcess`**
13. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
14. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.