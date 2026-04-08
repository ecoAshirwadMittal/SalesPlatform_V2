# Microflow Analysis: SUB_Order_SendOrderToOracle

### Requirements (Inputs):
- **$JSONContent** (A record of type: Object)

### Execution Steps:
1. **Run another process: "EcoATM_PWSIntegration.SUB_PWSConfiguration_GetOrCreate"
      - Store the result in a new variable called **$PWSConfiguration****
2. **Decision:** "toggle"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "EcoATM_PWSIntegration.CWS_PostToken"
      - Store the result in a new variable called **$Token****
4. **Decision:** "Token?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Run another process: "EcoATM_PWSIntegration.CWS_PostCreateOrder"
      - Store the result in a new variable called **$CreateOrderResponse****
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
