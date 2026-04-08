# Microflow Analysis: SUB_RetrieveLatestSummaryReportBySession

### Requirements (Inputs):
- **$Session** (A record of type: System.Session)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerBidSummaryReportHelperList****
2. **Take the list **$BuyerBidSummaryReportHelperList**, perform a [Sort], and call the result **$SortedReportObjects****
3. **Take the list **$SortedReportObjects**, perform a [Head], and call the result **$RecentSummaryReport****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
