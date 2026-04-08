# Microflow Analysis: ACT_SyncOrderHistory

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Search the Database for **EcoATM_PWS.Order** using filter: { [not(HasShipmentDetails)]
[EcoATM_PWS.Offer_Order/EcoATM_PWS.Offer/EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus/SystemStatus!='Canceled' or not(EcoATM_PWS.Offer_Order/EcoATM_PWS.Offer/EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus)]
 } (Call this list **$OrderList**)**
4. **Search the Database for **EcoATM_PWSIntegration.DeposcoConfig** using filter: { Show everything } (Call this list **$DeposcoConfig**)**
5. **Retrieve
      - Store the result in a new variable called **$DesposcoAPIsList****
6. **Take the list **$DesposcoAPIsList**, perform a [FindByExpression] where: { $currentObject/ServiceName = EcoATM_PWSIntegration.ENUM_DeposcoServices.OrderHistory }, and call the result **$DesposcoAPIsForOrderHistory****
7. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { Show everything } (Call this list **$OrderStatusList**)**
8. **Run another process: "EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword"
      - Store the result in a new variable called **$EncodedAuth****
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Permanently save **$undefined** to the database.**
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
