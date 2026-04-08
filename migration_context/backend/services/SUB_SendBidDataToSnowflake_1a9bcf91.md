# Microflow Analysis: SUB_SendBidDataToSnowflake

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Retrieve
      - Store the result in a new variable called **$BidDataList****
5. **Take the list **$BidDataList**, perform a [FilterByExpression] where: { $currentObject/BidAmount>0 }, and call the result **$BidDataList_PositiveBidAmount****
6. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
7. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
8. **Retrieve
      - Store the result in a new variable called **$Auction****
9. **Search the Database for **System.UserRole** using filter: { [System.UserRoles=$currentUser]
 } (Call this list **$UserRole**)**
10. **Create Variable**
11. **Create Variable**
12. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
13. **Create Object
      - Store the result in a new variable called **$JSON_INPUT****
14. **Change List**
15. **Create Object
      - Store the result in a new variable called **$BUYER_CODE****
16. **Change List**
17. **Create Object
      - Store the result in a new variable called **$WEEK_NUMBER****
18. **Change List**
19. **Create Object
      - Store the result in a new variable called **$YEAR_NUMBER****
20. **Change List**
21. **Create Object
      - Store the result in a new variable called **$ROUND_NUMBER****
22. **Change List**
23. **Create Object
      - Store the result in a new variable called **$SUBMITTED_BY****
24. **Change List**
25. **Create Object
      - Store the result in a new variable called **$IS_BIDDER_BUYER****
26. **Change List**
27. **Create Variable**
28. **Java Action Call
      - Store the result in a new variable called **$IsSuccess**** ⚠️ *(This step has a safety catch if it fails)*
29. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
30. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
