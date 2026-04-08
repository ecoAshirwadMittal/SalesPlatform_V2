# Microflow Analysis: ACO_UpdateOrderStatus_ByOffer

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Decision:** "Ordered or Pending?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
