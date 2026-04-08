# Microflow Analysis: DS_BidDataQueryHelper

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BidDataQueryHelperList****
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Take the list **$BidDataQueryHelperList**, perform a [Head], and call the result **$BidDataQueryHelper****
4. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidDataQueryHelper_SchedulingAuction] to: "empty"
      - Change [AuctionUI.BidDataQueryHelper_Auction] to: "empty"
      - Change [AuctionUI.BidDataQueryHelper_BuyerCode] to: "empty"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
