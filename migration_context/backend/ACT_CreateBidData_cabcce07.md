# Microflow Analysis: ACT_CreateBidData

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create Variable**
3. **Retrieve
      - Store the result in a new variable called **$BuyerCode_2****
4. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
5. **Decision:** "Not Empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Run another process: "Custom_Logging.SUB_Log_Warning"**
7. **Show Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
