# Microflow Analysis: ACT_SaveScheduleAuction

### Requirements (Inputs):
- **$AggInventoryHelper** (A record of type: AuctionUI.AggInventoryHelper)
- **$SchedulingAuction_Helper** (A record of type: AuctionUI.SchedulingAuction_Helper)

### Execution Steps:
1. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
2. **Retrieve
      - Store the result in a new variable called **$Auction****
3. **Retrieve
      - Store the result in a new variable called **$Week****
4. **Search the Database for **AuctionUI.Auction** using filter: { [AuctionUI.Auction_Week=$Week]
[AuctionStatus='Scheduled' or AuctionStatus='Started']
 } (Call this list **$AuctionExistsForWeek**)**
5. **Decision:** "AuctionExistsForWeek exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Retrieve
      - Store the result in a new variable called **$SchedulingAuctionList_existing****
7. **Decision:** "list not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Delete**
9. **Create List
      - Store the result in a new variable called **$SchedulingAuctionList_New****
10. **Create Object
      - Store the result in a new variable called **$SchedulingAuctionRound1****
11. **Change List**
12. **Create Object
      - Store the result in a new variable called **$SchedulingAuctionRound2****
13. **Change List**
14. **Create Object
      - Store the result in a new variable called **$SchedulingAuctionRound3****
15. **Change List**
16. **Permanently save **$undefined** to the database.**
17. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.HasAuction] to: "true
"
      - Change [AuctionUI.AggInventoryHelper.HasSchedule] to: "true
"**
18. **Update the **$undefined** (Object):
      - Change [AuctionUI.Auction.AuctionStatus] to: "AuctionUI.enum_SchedulingAuctionStatus.Scheduled
"
      - **Save:** This change will be saved to the database immediately.**
19. **Decision:** "send auction data to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
20. **Run another process: "AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async"**
21. **Close Form**
22. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
