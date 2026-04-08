# Microflow Analysis: Sub_BidDataSanitize_2

### Requirements (Inputs):
- **$BidDataList** (A record of type: AuctionUI.BidData)

### Execution Steps:
1. **Take the list **$BidDataList**, perform a [Filter] where: { -1 }, and call the result **$BidDataListMinus1****
2. **Take the list **$BidDataList**, perform a [Filter] where: { empty }, and call the result **$BidDataListMinusEmpty****
3. **Take the list **$BidDataList**, perform a [Filter] where: { 0 }, and call the result **$BidDataListMinusZero****
4. **Take the list **$BidDataListMinusEmpty**, perform a [Union], and call the result **$BidDataListMinusReady****
5. **Take the list **$BidDataList**, perform a [Subtract], and call the result **$BidDataListSansMinus1New****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Create List
      - Store the result in a new variable called **$UniqueBidDataList****
9. **Create List
      - Store the result in a new variable called **$BidDataDuplicateHelperList****
10. **Create List
      - Store the result in a new variable called **$BidDataDuplicateList****
11. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
12. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
13. **Create List
      - Store the result in a new variable called **$FinalChecker****
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Create List
      - Store the result in a new variable called **$BidDataList_2****
16. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
