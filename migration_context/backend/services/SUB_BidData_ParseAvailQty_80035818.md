# Microflow Analysis: SUB_BidData_ParseAvailQty

### Requirements (Inputs):
- **$AvailQty** (A record of type: Object)

### Execution Steps:
1. **Create Variable** ⚠️ *(This step has a safety catch if it fails)*
2. **Create Variable**
3. **Decision:** "parse decimal empty?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
