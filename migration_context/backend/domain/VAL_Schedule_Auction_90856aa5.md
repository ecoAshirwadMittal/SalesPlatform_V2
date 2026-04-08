# Microflow Analysis: VAL_Schedule_Auction

### Requirements (Inputs):
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Is R1 End Date and Time valid?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "Round 2 active?"
   - If [true] -> Move to: **Is End Date and Time not empty?**
   - If [false] -> Move to: **Finish**
4. **Decision:** "Is End Date and Time not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Decision:** "Round 3 active?"
   - If [true] -> Move to: **Is End Date and Time not empty?**
   - If [false] -> Move to: **Finish**
6. **Decision:** "Is End Date and Time not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
