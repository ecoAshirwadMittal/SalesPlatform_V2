# Microflow Analysis: Sub_BidDataSanitize

### Requirements (Inputs):
- **$BidDataList** (A record of type: EcoATM_Buyer.BidData)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "EcoATM_Buyer.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
3. **Decision:** "Execute?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Take the list **$BidDataList**, perform a [FilterByExpression] where: { $currentObject/Sanitized != true }, and call the result **$UnSanitized****
5. **Take the list **$BidDataList**, perform a [Subtract], and call the result **$Sanitized****
6. **Take the list **$UnSanitized**, perform a [FilterByExpression] where: { $currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true }, and call the result **$NewBidDataList****
7. **Take the list **$UnSanitized**, perform a [Subtract], and call the result **$DeletionList****
8. **Take the list **$NewBidDataList**, perform a [Filter] where: { -1 }, and call the result **$BidDataListMinus1****
9. **Take the list **$NewBidDataList**, perform a [Filter] where: { empty }, and call the result **$BidDataListMinusEmpty****
10. **Take the list **$NewBidDataList**, perform a [Filter] where: { 0 }, and call the result **$BidDataListMinusZero****
11. **Take the list **$BidDataListMinusEmpty**, perform a [Union], and call the result **$BidDataListMinusReady****
12. **Take the list **$NewBidDataList**, perform a [Subtract], and call the result **$BidDataListSansMinus1New****
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Create List
      - Store the result in a new variable called **$UniqueBidDataList****
16. **Create List
      - Store the result in a new variable called **$BidDataDuplicateHelperList****
17. **Create List
      - Store the result in a new variable called **$IgnoreList****
18. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
19. **Aggregate List
      - Store the result in a new variable called **$DeletionCount****
20. **Create Variable**
21. **Create Variable**
22. **Create Variable**
23. **Java Action Call
      - Store the result in a new variable called **$Variable****
24. **Take the list **$DeletionList**, perform a [ListRange], and call the result **$DeletionListSegment****
25. **Aggregate List
      - Store the result in a new variable called **$RetrievedCount****
26. **Delete**
27. **Change Variable**
28. **Java Action Call
      - Store the result in a new variable called **$Variable_2****
29. **Run another process: "Custom_Logging.SUB_Log_Info"**
30. **Change Variable**
31. **Decision:** "end of list?"
   - If [true] -> Move to: **Is Deletion List Empty?**
   - If [false] -> Move to: **Finish**
32. **Decision:** "Is Deletion List Empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
33. **Delete**
34. **Take the list **$Sanitized**, perform a [Union], and call the result **$FinalSanitized****
35. **Permanently save **$undefined** to the database.**
36. **Aggregate List
      - Store the result in a new variable called **$Count****
37. **Create Variable**
38. **Permanently save **$undefined** to the database.**
39. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
40. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
