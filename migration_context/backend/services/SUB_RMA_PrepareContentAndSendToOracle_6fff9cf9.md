# Microflow Analysis: SUB_RMA_PrepareContentAndSendToOracle

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Run another process: "EcoATM_RMA.SUB_RMA_PrepareOraclePayload"
      - Store the result in a new variable called **$JSONContent****
4. **Decision:** "JSONContent?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.JSONContent] to: "$JSONContent"**
6. **Run another process: "EcoATM_RMA.SUB_RMA_SendRMAToOracle"
      - Store the result in a new variable called **$CreateOrderResponse****
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
