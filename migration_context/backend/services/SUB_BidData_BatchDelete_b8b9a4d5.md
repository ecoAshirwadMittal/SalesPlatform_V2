# Microflow Analysis: SUB_BidData_BatchDelete

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction
and
AuctionUI.BidData_BidRound/AuctionUI.BidRound[ 
AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer = $Buyer]
] } (Call this list **$BidDataList_Buyer**)**
3. **Decision:** "not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Aggregate List
      - Store the result in a new variable called **$TotalItems****
5. **Create Variable**
6. **Create Variable**
7. **Create Variable**
8. **Java Action Call
      - Store the result in a new variable called **$Variable****
9. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction = $SchedulingAuction
and
AuctionUI.BidData_BidRound/AuctionUI.BidRound[ 
AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer = $Buyer]
]
 } (Call this list **$BidDataList**)**
10. **Aggregate List
      - Store the result in a new variable called **$RetrievedBidDataCount****
11. **Change Variable**
12. **Delete**
13. **Java Action Call
      - Store the result in a new variable called **$Variable_2****
14. **Log Message**
15. **Decision:** "end of list?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
16. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
