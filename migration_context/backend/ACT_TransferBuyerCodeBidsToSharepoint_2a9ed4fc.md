# Microflow Analysis: ACT_TransferBuyerCodeBidsToSharepoint

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$BidSubmitLog** (A record of type: AuctionUI.BidSubmitLog)

### Execution Steps:
1. **Search the Database for **EcoATM_BuyerManagement.AuctionsFeature** using filter: { Show everything } (Call this list **$AuctionsFeature**)**
2. **Retrieve
      - Store the result in a new variable called **$QualifiedBuyerCodes****
3. **Decision:** "! empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.QualifiedBuyerCodes.Submitted] to: "true"
      - Change [EcoATM_BuyerManagement.QualifiedBuyerCodes.SubmittedDateTime] to: "[%CurrentDateTime%]"
      - Change [EcoATM_BuyerManagement.QualifiedBuyerCodes_SubmittedBy] to: "$currentUser"**
5. **Decision:** "execute?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Retrieve
      - Store the result in a new variable called **$Auction****
7. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
8. **Create Object
      - Store the result in a new variable called **$NewAllBidsDoc****
9. **Retrieve
      - Store the result in a new variable called **$Week****
10. **Create List
      - Store the result in a new variable called **$AllBidsTempList****
11. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='AllBids_by_BuyerCode'] } (Call this list **$MxTemplate**)**
12. **Create List
      - Store the result in a new variable called **$AllBidDownloadList****
13. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory[AuctionUI.AggregatedInventory_Week/EcoATM_MDM.Week/AuctionUI.Auction_Week/AuctionUI.Auction=$Auction]
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode= $BuyerCode] } (Call this list **$BidDataList**)**
14. **Decision:** "DW buyer code?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
15. **Decision:** "Round 1?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
16. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
AuctionUI.AggregatedInventory_Week=$Week and
DWTotalQuantity > 0
] } (Call this list **$AgregatedInventoryList_DataWipe**)**
17. **Run another process: "EcoATM_Direct_Sharepoint.SUB_CreateBidDataDownload_DW"**
18. **Run another process: "AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode"
      - Store the result in a new variable called **$FileUploaded****
19. **Delete**
20. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidRound.UploadedToSharepoint] to: "$FileUploaded
"
      - Change [AuctionUI.BidRound.UploadToSharepointDateTime] to: "if($FileUploaded)
then [%CurrentDateTime%]
else $BidRound/UploadToSharepointDateTime
"
      - **Save:** This change will be saved to the database immediately.**
21. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
22. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
