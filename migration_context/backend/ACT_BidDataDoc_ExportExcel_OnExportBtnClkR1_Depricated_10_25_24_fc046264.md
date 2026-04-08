# Microflow Analysis: ACT_BidDataDoc_ExportExcel_OnExportBtnClkR1_Depricated_10_25_24

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Log Message**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidderRouterHelper.BuyerCode] to: "$NP_BuyerCodeSelect_Helper/Code"**
3. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='BidData'] } (Call this list **$MxTemplate**)**
4. **Decision:** "$MxTemplate!=empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Create Object
      - Store the result in a new variable called **$NewBidDataDoc****
6. **Create Object
      - Store the result in a new variable called **$ClickedRound****
7. **Run another process: "ECOATM_Buyer.SUB_GetBidDownload_Helper_Depricated_10_28_24"
      - Store the result in a new variable called **$BidDownload_HelperList****
8. **Permanently save **$undefined** to the database.**
9. **Run another process: "ECOATM_Buyer.SUB_BidDataCustomExcelExport"**
10. **Log Message**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
