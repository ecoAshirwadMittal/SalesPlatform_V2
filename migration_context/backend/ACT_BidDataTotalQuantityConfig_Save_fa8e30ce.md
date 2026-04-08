# Microflow Analysis: ACT_BidDataTotalQuantityConfig_Save

### Requirements (Inputs):
- **$BidDataTotalQuantityConfig** (A record of type: AuctionUI.BidDataTotalQuantityConfig)

### Execution Steps:
1. **Run another process: "AuctionUI.VAL_BidDataTotalQuantityConfig"
      - Store the result in a new variable called **$isValid****
2. **Decision:** "valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Permanently save **$undefined** to the database.**
4. **Close Form**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
