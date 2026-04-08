# Microflow Analysis: SUB_ClearAlBidDownloadList

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$AllBidDownloadList** (A record of type: AuctionUI.AllBidDownload)

### Execution Steps:
1. **Aggregate List
      - Store the result in a new variable called **$AllBidsCount****
2. **Change List** ⚠️ *(This step has a safety catch if it fails)*
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
