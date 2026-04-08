# Microflow Analysis: SUB_AuctionTimerHelper

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "AuctionUI.ACT_GetTimeOffset"
      - Store the result in a new variable called **$TimeZoneOffset****
3. **Create Variable**
4. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $Week] } (Call this list **$Auction**)**
5. **Take the list **$Auction**, perform a [Find] where: { 1 }, and call the result **$NewSchedulingAuction1****
6. **Take the list **$Auction**, perform a [Find] where: { 2 }, and call the result **$NewSchedulingAuction2****
7. **Take the list **$Auction**, perform a [Find] where: { 3 }, and call the result **$NewSchedulingAuction3****
8. **Create Variable**
9. **Create Variable**
10. **Create Variable**
11. **Create Object
      - Store the result in a new variable called **$NewAuctionTimerHelper****
12. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
