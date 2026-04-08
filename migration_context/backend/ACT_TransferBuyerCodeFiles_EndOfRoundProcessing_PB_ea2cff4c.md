# Microflow Analysis: ACT_TransferBuyerCodeFiles_EndOfRoundProcessing_PB

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Log Message**
2. **Java Action Call**
3. **Log Message**
4. **Retrieve
      - Store the result in a new variable called **$Auction****
5. **Search the Database for **AuctionUI.BuyerCode** using filter: { [
  (
    AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active'
  )
] } (Call this list **$ActiveBuyerCodeList**)**
6. **Search the Database for **AuctionUI.BuyerCode** using filter: { [AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[ Submitted = true
and
AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]
] } (Call this list **$SubmittedBuyerCodeList**)**
7. **Take the list **$ActiveBuyerCodeList**, perform a [Subtract], and call the result **$NotSubmittedActiveBuyerCodeList****
8. **Retrieve
      - Store the result in a new variable called **$Week****
9. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='AllBids_by_BuyerCode'] } (Call this list **$MxTemplate**)**
10. **Create Object
      - Store the result in a new variable called **$ExcelOutput_DW****
11. **Create Object
      - Store the result in a new variable called **$ExcelOutput_ALL****
12. **Create Variable**
13. **Create Variable**
14. **Create Object
      - Store the result in a new variable called **$NewAllBidsDoc_DW****
15. **Create Object
      - Store the result in a new variable called **$NewAllBidsDoc_ALL****
16. **Create List
      - Store the result in a new variable called **$AllBidDownloadList****
17. **Create List
      - Store the result in a new variable called **$DWAllBidDownloadList****
18. **Create List
      - Store the result in a new variable called **$BidRoundList_ToCommit****
19. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
AuctionUI.AggregatedInventory_Week=$Week
] } (Call this list **$AggregatedInventoryList_ALL**)**
20. **Take the list **$AggregatedInventoryList_ALL**, perform a [FilterByExpression] where: { $currentObject/DWTotalQuantity > 0 }, and call the result **$DWAggregatedInventoryList****
21. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.AggregatedInventory_Week=$Week]
[DWTotalQuantity > 0] } (Call this list **$AgregatedInventoryList_DW**)**
22. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
23. **Run another process: "EcoATM_Direct_Sharepoint.SUB_CheckSharepointTransferStatus"
      - Store the result in a new variable called **$Variable****
24. **Permanently save **$undefined** to the database.**
25. **Run another process: "EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete"**
26. **Run another process: "EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete"**
27. **Delete**
28. **Delete**
29. **Run another process: "EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteUnSubmittedUploadedBids"**
30. **Log Message**
31. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
