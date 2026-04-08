# Microflow Analysis: ACT_TransferBuyerCodeFiles_EndOfRoundProcessing

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Log Message**
2. **Java Action Call**
3. **Log Message**
4. **Create List
      - Store the result in a new variable called **$BidData_ToDelete****
5. **Search the Database for **AuctionUI.BuyerCode** using filter: { [
  (
    AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active'
  )
] } (Call this list **$BuyerCodeList_All**)**
6. **Search the Database for **AuctionUI.BuyerCode** using filter: { [AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[ Submitted = true
and
AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction ]
] } (Call this list **$BuyerCodeList_Submitted**)**
7. **Take the list **$BuyerCodeList_All**, perform a [Subtract], and call the result **$BuyerCodeList_NotSubmitted****
8. **Run another process: "EcoATM_Direct_Sharepoint.ACT_SetSharepointDownloadStart"**
9. **Retrieve
      - Store the result in a new variable called **$Auction****
10. **Retrieve
      - Store the result in a new variable called **$Week****
11. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='AllBids_by_BuyerCode'] } (Call this list **$MxTemplate**)**
12. **Create List
      - Store the result in a new variable called **$AllBidDownloadList****
13. **Create List
      - Store the result in a new variable called **$BidRoundList_ToCommit****
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Log Message**
16. **Run another process: "EcoATM_Direct_Sharepoint.SUB_CheckSharepointTransferStatus"
      - Store the result in a new variable called **$Variable****
17. **Permanently save **$undefined** to the database.**
18. **Run another process: "EcoATM_Direct_Sharepoint.SUB_BidData_BatchDeleteUnSubmittedUploadedBids"**
19. **Log Message**
20. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
