# Microflow Analysis: ACT_OrderStatus_Save

### Requirements (Inputs):
- **$OrderStatus** (A record of type: EcoATM_PWS.OrderStatus)

### Execution Steps:
1. **Run another process: "EcoATM_PWS.VAL_OrderStatus_IsValide"
      - Store the result in a new variable called **$IsValide****
2. **Decision:** "IsValide?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Permanently save **$undefined** to the database.**
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Close Form**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
