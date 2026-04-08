# Microflow Analysis: SUB_Order_PrepareContentAndSendToOracle

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)
- **$Order** (A record of type: EcoATM_PWS.Order)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Run another process: "EcoATM_PWS.SUB_Offer_PrepareOraclePayload"
      - Store the result in a new variable called **$JSONContent****
4. **Decision:** "JSONContent?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Order.JSONContent] to: "$JSONContent"**
6. **Run another process: "EcoATM_PWS.SUB_Order_SendOrderToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
7. **Search the Database for **Administration.Account** using filter: { [id=$currentUser]
 } (Call this list **$CurrentLoggedInUser**)**
8. **Run another process: "Custom_Logging.SUB_Log_Info"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
