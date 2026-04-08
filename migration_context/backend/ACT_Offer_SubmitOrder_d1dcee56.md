# Microflow Analysis: ACT_Offer_SubmitOrder

### Requirements (Inputs):
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)
- **$ENUM_OfferType** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
3. **Create Variable**
4. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
5. **Run another process: "Custom_Logging.SUB_Log_Info"**
6. **Search the Database for **EcoATM_PWS.BuyerOfferItem** using filter: { [EcoATM_PWS.BuyerOfferItem_BuyerOffer=$BuyerOffer]
[TotalPrice>0]
 } (Call this list **$BuyerOfferItemWithPriceList**)**
7. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
8. **Run another process: "EcoATM_PWS.SUB_BuyerOffer_CreateOffer"
      - Store the result in a new variable called **$Offer**** ⚠️ *(This step has a safety catch if it fails)*
9. **Decision:** "Offer Available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
10. **Run another process: "EcoATM_PWS.SUB_BuyerOffer_RemoveRecords"**
11. **Run another process: "EcoATM_PWS.SUB_Order_CreateFromOffer"
      - Store the result in a new variable called **$Order****
12. **Run another process: "EcoATM_PWS.SUB_Order_PrepareContentAndSendToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
13. **Search the Database for **Administration.Account** using filter: { [id=$currentUser]
 } (Call this list **$CurrentLoggedInUser**)**
14. **Decision:** "Response exist?"
   - If [true] -> Move to: **Is success?**
   - If [false] -> Move to: **Activity**
15. **Decision:** "Is success?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
16. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Order.OrderNumber] to: "$CreateOrderResponse/OrderNumber
"
      - Change [EcoATM_PWS.Order.OracleOrderStatus] to: "$CreateOrderResponse/ReturnMessage
"
      - Change [EcoATM_PWS.Order.OrderLine] to: "$CreateOrderResponse/OrderId
"
      - Change [EcoATM_PWS.Order.OracleHTTPCode] to: "$CreateOrderResponse/HTTPCode"
      - Change [EcoATM_PWS.Order.OracleJSONResponse] to: "$CreateOrderResponse/JSONResponse"
      - Change [EcoATM_PWS.Order.OrderDate] to: "[%CurrentDateTime%]"
      - Change [EcoATM_PWS.Order.IsSuccessful] to: "$CreateOrderResponse/ReturnCode='00'"
      - Change [EcoATM_PWS.Offer_Order] to: "$Offer"
      - Change [EcoATM_PWS.OrderCreatedBy_Account] to: "$CurrentLoggedInUser"
      - **Save:** This change will be saved to the database immediately.**
17. **Run another process: "EcoATM_PWS.SUB_RetrieveOrderStatus"
      - Store the result in a new variable called **$OrderStatus****
18. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferStatus] to: "EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]"
      - Change [EcoATM_PWS.Offer_OrderStatus] to: "$OrderStatus
"
      - **Save:** This change will be saved to the database immediately.**
19. **Run another process: "EcoATM_PWS.SUB_SendPWSPendingOrderEmail"**
20. **Run another process: "EcoATM_PWS.SUB_UpdateOfferDrawerStatus"**
21. **Run another process: "EcoATM_PWSIntegration.SUB_PWSErrorCode_GetMessage"
      - Store the result in a new variable called **$PWSResponseConfig****
22. **Close Form**
23. **Show Page**
24. **Create Object
      - Store the result in a new variable called **$NewUserMessage_1****
25. **Show Page**
26. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
27. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
