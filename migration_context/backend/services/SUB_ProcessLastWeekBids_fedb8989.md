# Microflow Analysis: SUB_ProcessLastWeekBids

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Log Message**
2. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code 
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week=$Last_Week
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true] } (Call this list **$Round1_BidDataList**)**
3. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code 
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=2]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week=$Last_Week
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true] } (Call this list **$Round2_BidDataList**)**
4. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code 
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=3]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week=$Last_Week
and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true] } (Call this list **$Round3_BidDataList**)**
5. **Create List
      - Store the result in a new variable called **$LastWeekBidData****
6. **Decision:** "Round3 bid data does not exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Log Message**
8. **Decision:** "Round2 bid data does not exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
9. **Log Message**
10. **Run another process: "EcoATM_BidData.ACT_AddCarryOverBidData"**
11. **Log Message**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
