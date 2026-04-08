# Microflow Analysis: SUB_DateTimeEmailBody

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GetTimeOffset"
      - Store the result in a new variable called **$TimeZoneOffset****
2. **Create Variable**
3. **Create Variable**
4. **Decision:** "11,12,13"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
5. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Create Variable**
7. **Create Variable**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
