# Microflow Analysis: SUB_ProcessHistoricalBids

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Log Message**
2. **Search the Database for **EcoATM_BidData.HistoricalBidData** using filter: { [buyer_code=$NP_BuyerCodeSelect_Helper/Code] } (Call this list **$HistoricalBidDataList**)**
3. **Create List
      - Store the result in a new variable called **$BidDataList****
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Run another process: "EcoATM_BidData.ACT_AddCarryOverBidData"**
6. **Log Message**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
