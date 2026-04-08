# Microflow Analysis: SUB_GenerateRound3QualifiedBuyerCodes

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
5. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [AuctionUI.BidData_BuyerCode/AuctionUI.BidData[BidAmount > 0]/AuctionUI.BidData_BidRound/AuctionUI.BidRound[Submitted=true]
/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction=$Auction] } (Call this list **$BuyerCodeList_Qualified**)**
6. **Run another process: "AuctionUI.SUB_GenerateQualifiedBuyerCodes"
      - Store the result in a new variable called **$BuyerCodeList****
7. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName_1****
8. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
