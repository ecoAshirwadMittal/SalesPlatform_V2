# Microflow Analysis: SUB_BidData_BatchDeleteAllBidsByAuction

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **AuctionUI.BidData** using filter: { [
AuctionUI.BidData_BidRound/AuctionUI.BidRound[
AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $Week]
] } (Call this list **$BidDataList_All**)**
3. **Aggregate List
      - Store the result in a new variable called **$TotalItems****
4. **Create Variable**
5. **Create Variable**
6. **Create Variable**
7. **Java Action Call
      - Store the result in a new variable called **$Variable****
8. **Search the Database for **AuctionUI.BidData** using filter: { [
AuctionUI.BidData_BidRound/AuctionUI.BidRound[
AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week = $Week]

] } (Call this list **$BidDataList**)**
9. **Aggregate List
      - Store the result in a new variable called **$RetrievedBidDataCount****
10. **Change Variable**
11. **Delete**
12. **Java Action Call
      - Store the result in a new variable called **$Variable_2****
13. **Run another process: "Custom_Logging.SUB_Log_Info"**
14. **Decision:** "end of list?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
15. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
