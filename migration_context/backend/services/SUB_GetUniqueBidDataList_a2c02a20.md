# Microflow Analysis: SUB_GetUniqueBidDataList

### Requirements (Inputs):
- **$DeviceAllocation** (A record of type: EcoATM_DA.DeviceAllocation)
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Search the Database for **AuctionUI.BidData** using filter: { [
$DeviceAllocation/Grade = Merged_Grade
and
$DeviceAllocation/ProductID = EcoID
and
$DAWeek/EcoATM_DA.DAWeek_Week = AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week/EcoATM_MDM.Week/id
and
AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted
]
 } (Call this list **$BidDataList**)**
2. **Create List
      - Store the result in a new variable called **$BidDataList_Unique****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
