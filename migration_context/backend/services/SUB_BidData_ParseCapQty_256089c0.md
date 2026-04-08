# Microflow Analysis: SUB_BidData_ParseCapQty

### Requirements (Inputs):
- **$CapQty** (A record of type: Object)

### Execution Steps:
1. **Create Variable** ⚠️ *(This step has a safety catch if it fails)*
2. **Create Variable**
3. **Decision:** "parse decimal empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Change Variable**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
