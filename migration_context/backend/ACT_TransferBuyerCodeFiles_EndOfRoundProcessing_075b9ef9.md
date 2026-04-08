# Microflow Analysis: ACT_TransferBuyerCodeFiles_EndOfRoundProcessing

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Java Action Call**
3. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
4. **Decision:** "Execute?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Run another process: "Custom_Logging.SUB_Log_Info"**
6. **Retrieve
      - Store the result in a new variable called **$Auction****
7. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [
  (
    EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active'
  )
] } (Call this list **$BuyerCodeList_All**)**
8. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[ Submitted = true
and
AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]
] } (Call this list **$BuyerCodeList_Submitted**)**
9. **Take the list **$BuyerCodeList_All**, perform a [Subtract], and call the result **$BuyerCodeList_NotSubmitted****
10. **Retrieve
      - Store the result in a new variable called **$Week****
11. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='AllBids_by_BuyerCode'] } (Call this list **$MxTemplate**)**
12. **Create Object
      - Store the result in a new variable called **$ExcelOutput_DW****
13. **Create Object
      - Store the result in a new variable called **$ExcelOutput_ALL****
14. **Create Variable**
15. **Create Variable**
16. **Create Object
      - Store the result in a new variable called **$NewAllBidsDoc_DW****
17. **Create Object
      - Store the result in a new variable called **$NewAllBidsDoc_ALL****
18. **Create List
      - Store the result in a new variable called **$AllBidDownloadList****
19. **Create List
      - Store the result in a new variable called **$AllBidDownloadList_DW****
20. **Create List
      - Store the result in a new variable called **$BidRoundList_ToCommit****
21. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
AuctionUI.AggregatedInventory_Week=$Week
] } (Call this list **$AggregatedInventoryList_ALL**)**
22. **Take the list **$AggregatedInventoryList_ALL**, perform a [FilterByExpression] where: { $currentObject/DWTotalQuantity > 0 }, and call the result **$DWAggregatedInventoryList****
23. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [AuctionUI.AggregatedInventory_Week=$Week]
[DWTotalQuantity > 0] } (Call this list **$AgregatedInventoryList_DW**)**
24. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
25. **Run another process: "EcoATM_Direct_Sharepoint.SUB_CheckSharepointTransferStatus"
      - Store the result in a new variable called **$Variable****
26. **Permanently save **$undefined** to the database.**
27. **Run another process: "EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete"**
28. **Run another process: "EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete"**
29. **Delete**
30. **Delete**
31. **Run another process: "EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteUnSubmittedUploadedBids"**
32. **Run another process: "Custom_Logging.SUB_Log_Info"**
33. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
