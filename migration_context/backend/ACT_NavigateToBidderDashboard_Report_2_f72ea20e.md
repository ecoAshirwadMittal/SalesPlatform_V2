# Microflow Analysis: ACT_NavigateToBidderDashboard_Report_2

### Requirements (Inputs):
- **$BuyerCodeSelectSearch_Helper** (A record of type: AuctionUI.BuyerCodeSelectSearch_Helper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$NP_BuyerCodeSelect_Helper****
2. **Close Form**
3. **Decision:** "BuyerCode exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Search the Database for **AuctionUI.Auction** using filter: { [
  (
    AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Started'
or
    AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/RoundStatus = 'Closed'
  )
] } (Call this list **$Auction_Active_1**)**
5. **Decision:** "Auction DOES NOT Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Show Message**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
