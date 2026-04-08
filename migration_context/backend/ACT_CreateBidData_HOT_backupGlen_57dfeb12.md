# Microflow Analysis: ACT_CreateBidData_HOT_backupGlen

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BidRound****
2. **Run another process: "AuctionUI.SUB_GetCurrentWeek"
      - Store the result in a new variable called **$Week****
3. **Search the Database for **EcoATM_Buyer.BidData** using filter: { [Code = $NP_BuyerCodeSelect_Helper/Code]
[EcoATM_Buyer.BidData_BidRound = $BidRound] } (Call this list **$Existing_BidDataList**)**
4. **Decision:** "No Existing Bid Data?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Create Variable**
6. **Run another process: "AuctionUI.ACT_CreateBidDataHelper_AggregatedList"
      - Store the result in a new variable called **$AgregatedInventory****
7. **Search the Database for **EcoATM_Buyer.BidData** using filter: { [
EcoATM_Buyer.BidData_BuyerCode/AuctionUI.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code 
and 
EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week=$Week
] } (Call this list **$AllRounds_BidDataList**)**
8. **Create List
      - Store the result in a new variable called **$BidData_List****
9. **Create List
      - Store the result in a new variable called **$PreviousRoundBidDataList****
10. **Create List
      - Store the result in a new variable called **$CurrentRoundBidDataList****
11. **Decision:** "Round 1?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
12. **Decision:** "Round 2?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
13. **Take the list **$AllRounds_BidDataList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_Buyer.BidData_BidRound= $BidRound }, and call the result **$CurrR2_BidDataList****
14. **Change List**
15. **Take the list **$AllRounds_BidDataList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1 }, and call the result **$PrevR1_BidDataList****
16. **Change List**
17. **Decision:** "Round 3?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
18. **Take the list **$AllRounds_BidDataList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_Buyer.BidData_BidRound= $BidRound }, and call the result **$CurrR3_BidDataList****
19. **Change List**
20. **Take the list **$AllRounds_BidDataList**, perform a [FilterByExpression] where: { $currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2 }, and call the result **$PrevR2_BidDataList****
21. **Decision:** "empty round 2 bid data?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
22. **Change List**
23. **Decision:** "DW buyer code?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
24. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
25. **Permanently save **$undefined** to the database.**
26. **Take the list **$BidData_List**, perform a [Sort], and call the result **$SortBidData_HelperList****
27. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
