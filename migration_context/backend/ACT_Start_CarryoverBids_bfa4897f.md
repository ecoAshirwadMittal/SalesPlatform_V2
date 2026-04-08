# Microflow Analysis: ACT_Start_CarryoverBids

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$BidderRouterHelper** (A record of type: AuctionUI.BidderRouterHelper)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)
- **$Parent_NPBuyerCodeSelectHelper_Gallery** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Retrieve
      - Store the result in a new variable called **$CurrentWeek****
5. **Run another process: "EcoATM_BidData.SUB_GetCurrentWeekMinusOne"
      - Store the result in a new variable called **$LastWeek****
6. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code 
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction=$LastWeek/AuctionUI.Auction_Week
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true] } (Call this list **$BidData_LastWeek**)**
7. **Decision:** "Last week  bid data exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
8. **Show Page**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
