# Microflow Analysis: ACT_NavigateToBidderDashboard_Report_3

### Requirements (Inputs):
- **$BuyerCode_3** (A record of type: AuctionUI.BuyerCode)

### Execution Steps:
1. **Create Object
      - Store the result in a new variable called **$NewBuyerCodeSelect_Helper****
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
3. **Close Form**
4. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
5. **Decision:** "BuyerCode exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Show Page**
7. **Run another process: "AuctionUI.ACT_OpenBidderDashboard"**
8. **Search the Database for **AuctionUI.Auction** using filter: { [
  (
    AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Started'
or
    AuctionUI.SchedulingAuction_Auction/AuctionUI.SchedulingAuction/Status = 'Closed'
  )
] } (Call this list **$Auction_Active_1**)**
9. **Decision:** "Auction DOES NOT Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Show Message**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
