# Microflow Analysis: Sub_SyncOrderHistory

### Requirements (Inputs):
- **$DesposcoAPIsForOrderHistory** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$IteratorOrder** (A record of type: EcoATM_PWS.Order)
- **$OrderStatusList** (A record of type: EcoATM_PWS.OrderStatus)
- **$DesposcoAPIsList** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$EncodedAuth** (A record of type: Object)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)

### Execution Steps:
1. **Decision:** "ordernumber exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "EcoATM_PWSIntegration.ACT_GenerateDeposcoV2Token"
      - Store the result in a new variable called **$AccessToken**** ⚠️ *(This step has a safety catch if it fails)*
5. **Change Variable**
6. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
7. **Run another process: "EcoATM_PWSIntegration.ACT_AuditRestAPICalls"**
8. **Import Xml**
9. **Take the list **$OrderHistoryResponseList**, perform a [Head], and call the result **$OrderHistory****
10. **Decision:** "OrderHistory avl?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
11. **Take the list **$OrderStatusList**, perform a [FindByExpression] where: { $currentObject/SystemStatus=$OrderHistory/OrderStatus }, and call the result **$OrderStatus****
12. **Retrieve
      - Store the result in a new variable called **$Offer****
13. **Retrieve
      - Store the result in a new variable called **$OrderStatus_2****
14. **Decision:** "change in status ?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
15. **Run another process: "EcoATM_PWS.SUB_Offer_UpdateSnowflake"**
16. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer_OrderStatus] to: "$OrderStatus"
      - **Save:** This change will be saved to the database immediately.**
17. **Decision:** "Shipped?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
18. **Retrieve
      - Store the result in a new variable called **$SKUQuantityList****
19. **Aggregate List
      - Store the result in a new variable called **$TotalOrderPackQuantity****
20. **Aggregate List
      - Store the result in a new variable called **$TotalCanceledPackQuantity****
21. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OrderPackQuantity] to: "round($TotalOrderPackQuantity)
"
      - Change [EcoATM_PWS.Offer.CanceledQuantity] to: "round($TotalCanceledPackQuantity)
"
      - **Save:** This change will be saved to the database immediately.**
22. **Run another process: "EcoATM_PWSIntegration.SUB_UpdateShipmentData"** ⚠️ *(This step has a safety catch if it fails)*
23. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
