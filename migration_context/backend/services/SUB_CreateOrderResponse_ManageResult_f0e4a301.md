# Microflow Analysis: SUB_CreateOrderResponse_ManageResult

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$Order** (A record of type: EcoATM_PWS.Order)
- **$CreateOrderResponse** (A record of type: EcoATM_PWSIntegration.OracleResponse)

### Execution Steps:
1. **Decision:** "Order Reponse?"
   - If [false] -> Move to: **Submicroflow**
   - If [true] -> Move to: **Submicroflow**
2. **Run another process: "EcoATM_PWS.SUB_RetrieveOrderStatus"
      - Store the result in a new variable called **$OrderStatus_1****
3. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.OfferStatus] to: "EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order"
      - Change [EcoATM_PWS.Offer.UpdateDate] to: "[%CurrentDateTime%]
"
      - Change [EcoATM_PWS.Offer.FinalOfferSubmittedOn] to: "[%CurrentDateTime%]"
      - Change [EcoATM_PWS.Offer_OrderStatus] to: "$OrderStatus_1
"
      - **Save:** This change will be saved to the database immediately.**
4. **Run another process: "EcoATM_PWS.SUB_SendPWSPendingOrderEmail"**
5. **Permanently save **$undefined** to the database.**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
