# Microflow Analysis: ACT_BidDataDoc_ExportExcel_OnBuyerCodeSelectR1_OldLogic

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name='BidData'] } (Call this list **$MxTemplate**)**
2. **Decision:** "$MxTemplate!=empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Variable**
4. **Create Object
      - Store the result in a new variable called **$NewBidDataDoc****
5. **Create Object
      - Store the result in a new variable called **$ClickedRound****
6. **Run another process: "ECOATM_Buyer.SUB_GetBidDownload_Helper"
      - Store the result in a new variable called **$BidDownload_HelperList****
7. **Permanently save **$undefined** to the database.**
8. **Run another process: "ECOATM_Buyer.SUB_BidDataGenerateReport"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
