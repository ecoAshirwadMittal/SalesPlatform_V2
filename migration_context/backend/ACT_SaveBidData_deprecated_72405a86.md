# Microflow Analysis: ACT_SaveBidData_deprecated

### Requirements (Inputs):
- **$BidData_Helper** (A record of type: AuctionUI.BidData_Helper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BidRound****
2. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound=$BidRound
and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/ecoID=$BidData_Helper/EcoID
and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Merged_Grade=$BidData_Helper/ecoGrade
] } (Call this list **$BidData**)**
3. **Decision:** "check if bidData exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Object
      - Store the result in a new variable called **$NewBidData****
5. **Validation Feedback**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
