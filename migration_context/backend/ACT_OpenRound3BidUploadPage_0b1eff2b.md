# Microflow Analysis: ACT_OpenRound3BidUploadPage

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)
- **$CompanyName** (A record of type: Object)
- **$RoundThreeBuyersDataReport** (A record of type: AuctionUI.RoundThreeBuyersDataReport)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [AuctionUI.SchedulingAuction_Auction = $Auction and Round = 3] } (Call this list **$Round3SchedulingAuction**)**
3. **Search the Database for **EcoATM_BuyerManagement.Buyer** using filter: { [
  (
    CompanyName = $CompanyName
  )
] } (Call this list **$Buyer**)**
4. **Show Page**
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
