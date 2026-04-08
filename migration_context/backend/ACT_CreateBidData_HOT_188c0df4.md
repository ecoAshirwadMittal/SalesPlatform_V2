# Microflow Analysis: ACT_CreateBidData_HOT

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$BidRound****
3. **Create Variable**
4. **Retrieve
      - Store the result in a new variable called **$BuyerCode_2****
5. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
6. **Decision:** "Not Empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
7. **Run another process: "Custom_Logging.SUB_Log_Warning"**
8. **Show Message**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
