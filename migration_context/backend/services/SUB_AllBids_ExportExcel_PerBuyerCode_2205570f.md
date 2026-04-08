# Microflow Analysis: SUB_AllBids_ExportExcel_PerBuyerCode

### Requirements (Inputs):
- **$AllBidDownloadList** (A record of type: AuctionUI.AllBidDownload)
- **$AllBidsTempList** (A record of type: AuctionUI.AllBidsZipTempList)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)
- **$Auction** (A record of type: AuctionUI.Auction)
- **$AuctionSubFolder** (A record of type: Object)
- **$UserSubmitedBidSubmitLog** (A record of type: AuctionUI.BidSubmitLog)

### Execution Steps:
1. **Log Message**
2. **Decision:** "$MxTemplate!=empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$NewAllBidsDoc****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Permanently save **$undefined** to the database.**
6. **Create Object
      - Store the result in a new variable called **$NewAllBidsZipTempList****
7. **Java Action Call
      - Store the result in a new variable called **$Document****
8. **Run another process: "AuctionUI.Sub_GetAuctionEndDate"
      - Store the result in a new variable called **$LastSchedulingAuction****
9. **Update the **$undefined** (Object):
      - Change [System.FileDocument.Name] to: "$BuyerCode/Code+'_'+formatDateTime($LastSchedulingAuction/End_DateTime, 'yyyyMMdd')
+'.xlsx'
"
      - Change [System.FileDocument.DeleteAfterDownload] to: "false"**
10. **Change List**
11. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
12. **Create Variable**
13. **Create Variable**
14. **Run another process: "AuctionUI.ACT_LogSendToSharepoint"
      - Store the result in a new variable called **$BidSubmitLog_Upload****
15. **Run another process: "EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate"
      - Store the result in a new variable called **$UploadSuccess**** ⚠️ *(This step has a safety catch if it fails)*
16. **Decision:** "Uploaded?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
17. **Run another process: "AuctionUI.ACT_UpdateBidSubmitLog"
      - Store the result in a new variable called **$BidSubmitLog_2****
18. **Delete**
19. **Permanently save **$undefined** to the database.**
20. **Log Message**
21. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
