# Microflow Analysis: ACT_SendBidstoSharepoint_perBuyerCode_Admin

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Log Message**
2. **Run another process: "AuctionUI.ACT_CreateBidSubmitLog"
      - Store the result in a new variable called **$BidSubmitLog****
3. **Run another process: "EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint"**
4. **Log Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
