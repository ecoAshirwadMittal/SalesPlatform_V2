# Microflow Analysis: VAL_Round1_End_DateTime

### Requirements (Inputs):
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Decision:** "End Date and Time"
   - If [true] -> Move to: **End Date and Time**
   - If [false] -> Move to: **Activity**
4. **Decision:** "End Date and Time"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Decision:** "Check condition"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
