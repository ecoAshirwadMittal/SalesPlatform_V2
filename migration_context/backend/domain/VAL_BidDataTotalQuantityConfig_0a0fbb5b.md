# Microflow Analysis: VAL_BidDataTotalQuantityConfig

### Requirements (Inputs):
- **$BidDataTotalQuantityConfig_current** (A record of type: AuctionUI.BidDataTotalQuantityConfig)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "is ecoid not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
3. **Change Variable**
4. **Validation Feedback**
5. **Decision:** "is grade not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Decision:** "is additional quantity not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
7. **Change Variable**
8. **Validation Feedback**
9. **Decision:** "is additional quantity not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
10. **Change Variable**
11. **Validation Feedback**
12. **Decision:** "eco id and merged grade not empty?"
   - If [true] -> Move to: **Submicroflow**
   - If [false] -> Move to: **Finish**
13. **Run another process: "AuctionUI.VAL_TotalQuantityAlreadyExists"
      - Store the result in a new variable called **$AlreadyExists****
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
