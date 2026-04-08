# Microflow Analysis: ACT_TransferBuyerCodeFiles_EndOfRoundProcessing_2

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Log Message**
2. **Create List
      - Store the result in a new variable called **$BidData_ToDelete****
3. **Search the Database for **AuctionUI.BuyerCode** using filter: { [
  (
    AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active'
  )
] } (Call this list **$BuyerCodeList_All**)**
4. **Search the Database for **AuctionUI.BuyerCode** using filter: { [AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[ Submitted = true
and
AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]
] } (Call this list **$BuyerCodeList_Submitted**)**
5. **Take the list **$BuyerCodeList_All**, perform a [Subtract], and call the result **$BuyerCodeList_NotSubmitted****
6. **Run another process: "EcoATM_Direct_Sharepoint.ACT_SetSharepointDownloadStart"**
7. **Retrieve
      - Store the result in a new variable called **$Auction****
8. **Retrieve
      - Store the result in a new variable called **$Week****
9. **Create List
      - Store the result in a new variable called **$BidDataList_empty****
10. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='AllBids_by_BuyerCode'] } (Call this list **$MxTemplate**)**
11. **Create Object
      - Store the result in a new variable called **$ExcelOutput_DW****
12. **Create Object
      - Store the result in a new variable called **$ExcelOutput_ALL****
13. **Create Variable**
14. **Create Variable**
15. **Create Object
      - Store the result in a new variable called **$NewAllBidsDoc_DW****
16. **Create Object
      - Store the result in a new variable called **$NewAllBidsDoc_ALL****
17. **Create List
      - Store the result in a new variable called **$AllBidDownloadList****
18. **Create List
      - Store the result in a new variable called **$BidRoundList_ToCommit****
19. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
20. **Log Message**
21. **Run another process: "EcoATM_Direct_Sharepoint.SUB_CheckSharepointTransferStatus"
      - Store the result in a new variable called **$Variable****
22. **Permanently save **$undefined** to the database.**
23. **Run another process: "EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete"**
24. **Run another process: "EcoATM_Direct_Sharepoint.SUB_AllBidDataDownload_BatchDelete"**
25. **Delete**
26. **Delete**
27. **Run another process: "EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteUnSubmittedUploadedBids"**
28. **Log Message**
29. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
