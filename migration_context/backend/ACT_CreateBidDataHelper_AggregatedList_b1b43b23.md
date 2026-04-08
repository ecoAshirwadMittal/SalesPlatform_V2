# Microflow Analysis: ACT_CreateBidDataHelper_AggregatedList

### Requirements (Inputs):
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Round2"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Code Type = Data_Wipe**
3. **Run another process: "AuctionUI.ACT_Round2AggregatedInventory"
      - Store the result in a new variable called **$AggregatedInventoryList****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
