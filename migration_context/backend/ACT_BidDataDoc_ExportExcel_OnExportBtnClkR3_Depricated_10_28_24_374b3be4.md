# Microflow Analysis: ACT_BidDataDoc_ExportExcel_OnExportBtnClkR3_Depricated_10_28_24

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Log Message**
2. **Java Action Call
      - Store the result in a new variable called **$Variable****
3. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidderRouterHelper.BuyerCode] to: "$NP_BuyerCodeSelect_Helper/Code"**
4. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='BidData'] } (Call this list **$MxTemplate**)**
5. **Decision:** "$MxTemplate!=empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Create Object
      - Store the result in a new variable called **$NewBidDataDoc****
7. **Create Object
      - Store the result in a new variable called **$ClickedRound****
8. **Run another process: "ECOATM_Buyer.SUB_GetBidDownload_Helper"
      - Store the result in a new variable called **$BidDownload_HelperList****
9. **Permanently save **$undefined** to the database.**
10. **Run another process: "ECOATM_Buyer.SUB_BidDataCustomExcelExport"**
11. **Java Action Call
      - Store the result in a new variable called **$Variable_2****
12. **Log Message**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
