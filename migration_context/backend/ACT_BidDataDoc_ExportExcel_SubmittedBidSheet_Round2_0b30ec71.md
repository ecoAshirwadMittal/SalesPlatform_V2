# Microflow Analysis: ACT_BidDataDoc_ExportExcel_SubmittedBidSheet_Round2

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidderRouterHelper.BuyerCode] to: "$NP_BuyerCodeSelect_Helper/Code
"**
3. **Search the Database for **AuctionUI.Auction** using filter: { Show everything } (Call this list **$Auction_MostRecent**)**
4. **Decision:** "Auction found?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Run another process: "Custom_Logging.SUB_Log_Error"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
