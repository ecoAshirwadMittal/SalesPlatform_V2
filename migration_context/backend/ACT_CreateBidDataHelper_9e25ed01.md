# Microflow Analysis: ACT_CreateBidDataHelper

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BidRound****
2. **Retrieve
      - Store the result in a new variable called **$BidData_HelperList_2****
3. **Delete**
4. **Run another process: "AuctionUI.SUB_GetCurrentWeek"
      - Store the result in a new variable called **$Week****
5. **Retrieve
      - Store the result in a new variable called **$Auction****
6. **Create Variable**
7. **Run another process: "AuctionUI.ACT_CreateBidDataHelper_AggregatedList"
      - Store the result in a new variable called **$AgregatedInventory****
8. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code 
and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week=$Week] } (Call this list **$AllRounds_BidDataList**)**
9. **Create List
      - Store the result in a new variable called **$BidData_HelperList****
10. **Retrieve
      - Store the result in a new variable called **$BidDataList_Old****
11. **Create List
      - Store the result in a new variable called **$PreviousRoundBidDataList****
12. **Create List
      - Store the result in a new variable called **$CurrentRoundBidDataList****
13. **Decision:** "Round 1?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
14. **Decision:** "Round 2?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
15. **Take the list **$AllRounds_BidDataList**, perform a [FilterByExpression] where: { $currentObject/AuctionUI.BidData_BidRound= $BidRound }, and call the result **$CurrR2_BidDataList****
16. **Change List**
17. **Take the list **$AllRounds_BidDataList**, perform a [FilterByExpression] where: { $currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1 }, and call the result **$PrevR1_BidDataList****
18. **Change List**
19. **Decision:** "Round 3?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
20. **Decision:** "DW buyer code?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Finish**
21. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
22. **Take the list **$BidData_HelperList**, perform a [Sort], and call the result **$SortedSalesBidData_HelperList****
23. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
