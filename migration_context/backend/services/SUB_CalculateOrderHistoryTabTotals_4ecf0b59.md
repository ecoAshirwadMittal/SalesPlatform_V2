# Microflow Analysis: SUB_CalculateOrderHistoryTabTotals

### Requirements (Inputs):
- **$OrderHistoryHelper** (A record of type: EcoATM_PWS.OrderHistoryHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_PWS.OfferAndOrdersView** using filter: { Show everything } (Call this list **$OfferAndOrdersViewList_All**)**
5. **Aggregate List
      - Store the result in a new variable called **$CountAll****
6. **Search the Database for **EcoATM_PWS.OfferAndOrdersView** using filter: { [
	OrderStatus = 'In Process'
	or
	OrderStatus = 'Offer Pending'
	or
	OrderStatus = 'Awaiting Carrier Pickup'
]
 } (Call this list **$OfferAndOrdersViewList_InProcess**)**
7. **Aggregate List
      - Store the result in a new variable called **$CountInProcess****
8. **Search the Database for **EcoATM_PWS.OfferAndOrdersView** using filter: { [
	OrderStatus!='In Process'
	and
	OrderStatus!='Offer Pending'
	and
	OrderStatus!='Awaiting Carrier Pickup'
]
 } (Call this list **$OfferAndOrdersViewList_Complete**)**
9. **Aggregate List
      - Store the result in a new variable called **$CountComplete****
10. **Search the Database for **EcoATM_PWS.OfferAndOrdersView** using filter: { [LastUpdateDate >= $OrderHistoryHelper/RecentDateCutoff] } (Call this list **$OfferAndOrdersViewList_Recent**)**
11. **Aggregate List
      - Store the result in a new variable called **$CountRecent****
12. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.OrderHistoryHelper.CountAll] to: "$CountAll"
      - Change [EcoATM_PWS.OrderHistoryHelper.CountComplete] to: "$CountComplete"
      - Change [EcoATM_PWS.OrderHistoryHelper.CountRecent] to: "$CountRecent"
      - Change [EcoATM_PWS.OrderHistoryHelper.CountInProcess] to: "$CountInProcess"**
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
