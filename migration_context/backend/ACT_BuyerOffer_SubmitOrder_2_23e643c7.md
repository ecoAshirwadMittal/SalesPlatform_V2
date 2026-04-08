# Microflow Analysis: ACT_BuyerOffer_SubmitOrder_2

### Requirements (Inputs):
- **$BuyerOffer** (A record of type: EcoATM_PWS.BuyerOffer)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_PWS.BuyerOfferItem** using filter: { [EcoATM_PWS.BuyerOfferItem_BuyerOffer=$BuyerOffer]
[TotalPrice>0]
 } (Call this list **$BuyerOfferItemWithPriceList**)**
5. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
7. **Run another process: "EcoATM_PWS.SUB_BuyerOffer_CreateOfferAndOrder"
      - Store the result in a new variable called **$Offer****
8. **Retrieve
      - Store the result in a new variable called **$Order****
9. **Run another process: "EcoATM_PWS.SUB_Offer_PrepareOraclePayload"
      - Store the result in a new variable called **$JSONContent****
10. **Run another process: "EcoATM_PWS.SUB_Order_SendOrderToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
11. **Decision:** "Response exist?"
   - If [true] -> Move to: **Is success?**
   - If [false] -> Move to: **Activity**
12. **Decision:** "Is success?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
13. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferStatus] to: "EcoATM_PWS.ENUM_PWSOrderStatus.Ordered
"**
14. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Order.OrderNumber] to: "$CreateOrderResponse/OrderNumber
"
      - Change [EcoATM_PWS.Order.OrderStatus] to: "$CreateOrderResponse/ReturnMessage
"
      - Change [EcoATM_PWS.Order.OrderLine] to: "$CreateOrderResponse/OrderId
"
      - Change [EcoATM_PWS.Order.JSONContent] to: "$JSONContent
"
      - **Save:** This change will be saved to the database immediately.**
15. **Run another process: "EcoATM_PWS.SUB_BuyerOffer_RemoveRecords"**
16. **Close Form**
17. **Show Page**
18. **Create Object
      - Store the result in a new variable called **$SuccessUserMessage****
19. **Show Page**
20. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
21. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
