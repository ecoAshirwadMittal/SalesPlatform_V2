# Microflow Analysis: ACT_Offer_SubmitOrder_2

### Requirements (Inputs):
- **$FinalOffer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_PWS.OfferItem** using filter: { [EcoATM_PWS.OfferItem_Offer=$FinalOffer]
[SalesOfferItemStatus='Accept' or (SalesOfferItemStatus='Counter' and BuyerCounterStatus='Accept')] } (Call this list **$AcceptedOfferItemList**)**
3. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
5. **Run another process: "EcoATM_PWS.SUB_Offer_CreateOrder"
      - Store the result in a new variable called **$Order****
6. **Run another process: "EcoATM_PWS.SUB_Offer_PrepareOraclePayload"
      - Store the result in a new variable called **$JSONContent****
7. **Run another process: "EcoATM_PWS.SUB_Order_SendOrderToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
8. **Decision:** "Response exist?"
   - If [true] -> Move to: **Is success?**
   - If [false] -> Move to: **Activity**
9. **Decision:** "Is success?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferStatus] to: "EcoATM_PWS.ENUM_PWSOrderStatus.Ordered"
      - **Save:** This change will be saved to the database immediately.**
11. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Order.OrderNumber] to: "$CreateOrderResponse/OrderNumber
"
      - Change [EcoATM_PWS.Order.OrderStatus] to: "$CreateOrderResponse/ReturnMessage
"
      - Change [EcoATM_PWS.Order.OrderLine] to: "$CreateOrderResponse/OrderId
"
      - Change [EcoATM_PWS.Order.JSONContent] to: "$JSONContent
"
      - **Save:** This change will be saved to the database immediately.**
12. **Close Form**
13. **Show Page**
14. **Create Object
      - Store the result in a new variable called **$SuccessUserMessage****
15. **Show Page**
16. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
