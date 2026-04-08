# Microflow Analysis: VAL_DeviceBuyer

### Requirements (Inputs):
- **$DeviceBuyer** (A record of type: EcoATM_DA.DeviceBuyer)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "BuyerCode"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "Has Bid"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
