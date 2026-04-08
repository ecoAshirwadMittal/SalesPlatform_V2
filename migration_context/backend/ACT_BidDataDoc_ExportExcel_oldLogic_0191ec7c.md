# Microflow Analysis: ACT_BidDataDoc_ExportExcel_oldLogic

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Log Message**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidderRouterHelper.BuyerCode] to: "$NP_BuyerCodeSelect_Helper/Code"**
3. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='BidDataExport']
 } (Call this list **$MxTemplate**)**
4. **Decision:** "$MxTemplate!=empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Create Object
      - Store the result in a new variable called **$NewBidDataDoc****
6. **Create Object
      - Store the result in a new variable called **$ClickedRound****
7. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
8. **Log Message**
9. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
10. **Log Message**
11. **Run another process: "AuctionUI.SUB_BidRound_CreateNew"
      - Store the result in a new variable called **$BidRound****
12. **Log Message**
13. **Run another process: "AuctionUI.SUB_GetCurrentWeek"
      - Store the result in a new variable called **$Week****
14. **Log Message**
15. **Run another process: "AuctionUI.ACT_CreateBidDataHelper_AggregatedList"
      - Store the result in a new variable called **$AgregatedInventory****
16. **Decision:** "AggregatedInventory List?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
17. **Run another process: "AuctionUI.SUB_BidDownloadHelper_CreateList"
      - Store the result in a new variable called **$BidDownload_HelperList****
18. **Decision:** "BidDownload_HelperList?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
19. **Log Message**
20. **Permanently save **$undefined** to the database.**
21. **Run another process: "AuctionUI.SUB_BidDataCustomExcelExport"**
22. **Log Message**
23. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='BidDataTest']
 } (Call this list **$MxTemplate_1**)**
24. **Decision:** "$MxTemplate!=empty"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
25. **Log Message**
26. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
