# Microflow Analysis: ACT_Offer_SubmitOrder

### Requirements (Inputs):
- **$FinalOffer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "EcoATM_PWS.SUB_Order_CreateFromOffer"
      - Store the result in a new variable called **$Order****
5. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
7. **Run another process: "EcoATM_PWS.SUB_Order_PrepareContentAndSendToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
8. **Decision:** "Response exist?"
   - If [true] -> Move to: **Is success?**
   - If [false] -> Move to: **Activity**
9. **Decision:** "Is success?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
