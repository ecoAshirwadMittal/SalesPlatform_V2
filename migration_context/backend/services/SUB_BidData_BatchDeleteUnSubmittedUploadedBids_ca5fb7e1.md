# Microflow Analysis: SUB_BidData_BatchDeleteUnSubmittedUploadedBids

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Search the Database for **AuctionUI.BidData** using filter: { [
AuctionUI.BidData_BidRound/AuctionUI.BidRound[
Submitted = false
and
AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction]
] } (Call this list **$BidDataList_All**)**
2. **Aggregate List
      - Store the result in a new variable called **$TotalItems****
3. **Create Variable**
4. **Create Variable**
5. **Create Variable**
6. **Java Action Call
      - Store the result in a new variable called **$Variable****
7. **Search the Database for **AuctionUI.BidData** using filter: { [
AuctionUI.BidData_BidRound/AuctionUI.BidRound[
Submitted = false
and
AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction]
] } (Call this list **$BidDataList**)**
8. **Aggregate List
      - Store the result in a new variable called **$RetrievedBidDataCount****
9. **Change Variable**
10. **Delete**
11. **Java Action Call
      - Store the result in a new variable called **$Variable_2****
12. **Log Message**
13. **Decision:** "end of list?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
