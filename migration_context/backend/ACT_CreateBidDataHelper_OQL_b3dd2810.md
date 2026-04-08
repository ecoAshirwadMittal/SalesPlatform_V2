# Microflow Analysis: ACT_CreateBidDataHelper_OQL

### Requirements (Inputs):
- **$BuyerCodeSelect_Helper** (A record of type: AuctionUI.BuyerCodeSelect_Helper)

### Execution Steps:
1. **Run another process: "AuctionUI.SUB_GetCurrentWeek"
      - Store the result in a new variable called **$Week****
2. **Create Variable**
3. **Java Action Call
      - Store the result in a new variable called **$response****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
