# Microflow Analysis: SUB_AllBidDownload_Create_C_YNY_G_YNN_DW

### Requirements (Inputs):
- **$AggregatedInventory** (A record of type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (A record of type: AuctionUI.BidData)

### Execution Steps:
1. **Create Object
      - Store the result in a new variable called **$CreateC_YNY_G_YNN_DW****
2. **Decision:** "BidData not empty?"
   - If [true] -> Move to: **Bid quantity 
Empty**
   - If [false] -> Move to: **Activity**
3. **Decision:** "Bid quantity 
Empty"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Bid quantity zero**
4. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload.C_YNY_G_YNNQuantityCap] to: "empty"**
5. **Decision:** "Bid Amount
Not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload.C_YNY_G_YNN] to: "'$0.00'"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
