# Microflow Analysis: ACT_ShowBidderDashboardPage

### Requirements (Inputs):
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.BuyerCodeSelect_Helper)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$AuctionTimerHelper** (A record of type: AuctionUI.AuctionTimerHelper)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Decision:** "BidRound Exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Run another process: "Custom_Logging.SUB_Log_Warning"
      - Store the result in a new variable called **$Log****
4. **Show Message**
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
