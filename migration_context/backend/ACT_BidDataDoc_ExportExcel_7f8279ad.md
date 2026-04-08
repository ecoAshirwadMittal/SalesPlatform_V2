# Microflow Analysis: ACT_BidDataDoc_ExportExcel

### Requirements (Inputs):
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Update the **$undefined** (Object):
      - Change [AuctionUI.BidderRouterHelper.BuyerCode] to: "$NP_BuyerCodeSelect_Helper/Code"**
3. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
4. **Decision:** "Sched Auct found?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Run another process: "Custom_Logging.SUB_Log_Error"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
