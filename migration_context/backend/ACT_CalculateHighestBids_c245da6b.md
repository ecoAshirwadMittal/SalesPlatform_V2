# Microflow Analysis: ACT_CalculateHighestBids

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$Auction****
3. **Retrieve
      - Store the result in a new variable called **$Week****
4. **Retrieve
      - Store the result in a new variable called **$AggregatedInventoryList_1****
5. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/id = $Week]
[AuctionUI.BidData_AggregatedInventory/AuctionUI.BidData[BidAmount > 0]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted = true]]

 } (Call this list **$AggregatedInventoryList**)**
6. **Run another process: "AuctionUI.SUB_CalcHighBids_ResetHighBids"**
7. **Create List
      - Store the result in a new variable called **$All_BidDataList****
8. **Create List
      - Store the result in a new variable called **$HighestBidDataList_toCommit****
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Permanently save **$undefined** to the database.**
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
