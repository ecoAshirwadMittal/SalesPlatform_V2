# Microflow Analysis: SUB_UpdateShipmentData

### Requirements (Inputs):
- **$Order** (A record of type: EcoATM_PWS.Order)
- **$OrderHistory** (A record of type: EcoATM_PWSIntegration.OrderHistory)
- **$DesposcoAPIsList** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$Auth** (A record of type: Object)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$ShipmentDetailList_Existing****
2. **Delete**
3. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
4. **Create List
      - Store the result in a new variable called **$IMEIDetailList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Take the list **$DesposcoAPIsList**, perform a [FindByExpression] where: { $currentObject/ServiceName= EcoATM_PWSIntegration.ENUM_DeposcoServices.Shipment }, and call the result **$ShipmentAPI****
7. **Retrieve
      - Store the result in a new variable called **$ShipToAddress****
8. **Retrieve
      - Store the result in a new variable called **$ShipToContact****
9. **Retrieve
      - Store the result in a new variable called **$ShipmentdataList****
10. **Retrieve
      - Store the result in a new variable called **$ShipmentDetailList****
11. **Run another process: "EcoATM_PWSIntegration.SUB_RetrieveShipmentDetails"
      - Store the result in a new variable called **$Shipment****
12. **Decision:** "shipment exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
13. **Retrieve
      - Store the result in a new variable called **$LineList****
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Aggregate List
      - Store the result in a new variable called **$TotalShippedPrice****
16. **Aggregate List
      - Store the result in a new variable called **$SumShippedQty****
17. **Take the list **$OfferItemList**, perform a [FilterByExpression] where: { $currentObject/ShippedQty>0 }, and call the result **$ShippedOfferItems****
18. **Aggregate List
      - Store the result in a new variable called **$CountOfSKUs****
19. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { [SystemStatus='Ship Complete']
 } (Call this list **$ShipCompleteStatus**)**
20. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Order.HasShipmentDetails] to: "true
"
      - Change [EcoATM_PWS.Order.ShippedTotalQuantity] to: "$SumShippedQty
"
      - Change [EcoATM_PWS.Order.ShippedTotalSKU] to: "$CountOfSKUs
"
      - Change [EcoATM_PWS.Order.ShipDate] to: "$OrderHistory/ActualShipDate
"
      - Change [EcoATM_PWS.Order.ShipMethod] to: "$OrderHistory/ShipVia
"
      - Change [EcoATM_PWS.Order.ShipToAddress] to: "$ShipToAddress/Line1 + ', '+$ShipToAddress/City +', '+$ShipToAddress/Country
"
      - Change [EcoATM_PWS.Order.ShippedTotalPrice] to: "$TotalShippedPrice"
      - **Save:** This change will be saved to the database immediately.**
21. **Retrieve
      - Store the result in a new variable called **$Offer****
22. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer_OrderStatus] to: "$ShipCompleteStatus
"
      - Change [EcoATM_PWS.Offer.ShippedQuantity] to: "$SumShippedQty
"
      - **Save:** This change will be saved to the database immediately.**
23. **Permanently save **$undefined** to the database.**
24. **Permanently save **$undefined** to the database.**
25. **Permanently save **$undefined** to the database.**
26. **Decision:** "all devices shipped?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
27. **Run another process: "EcoATM_PWS.SUB_ImeiData_UpdateSnowflake"**
28. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
