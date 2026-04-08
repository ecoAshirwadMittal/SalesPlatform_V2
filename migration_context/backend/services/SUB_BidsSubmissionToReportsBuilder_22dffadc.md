# Microflow Analysis: SUB_BidsSubmissionToReportsBuilder

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Log Message**
2. **Create Variable**
3. **Retrieve
      - Store the result in a new variable called **$BidDataList****
4. **Run another process: "AuctionUI.Sub_BidDataSanitize"
      - Store the result in a new variable called **$BidDataList_Sanitized**** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "AuctionUI.SUB_BuildSummaryReportObject"
      - Store the result in a new variable called **$BuyerBidSummaryReport****
6. **Run another process: "AuctionUI.SUB_BidsSubmissionToBuyerSummaryReportBuilder"**
7. **Create Variable**
8. **Log Message**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
