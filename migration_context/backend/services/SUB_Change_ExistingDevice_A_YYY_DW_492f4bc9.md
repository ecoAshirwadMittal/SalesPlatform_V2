# Microflow Analysis: SUB_Change_ExistingDevice_A_YYY_DW

### Requirements (Inputs):
- **$AggregatedInventory** (A record of type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$BidData** (A record of type: AuctionUI.BidData)
- **$AllBidDownload** (A record of type: AuctionUI.AllBidDownload)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload.MAXofGradeA_YYY] to: "'$'+toString($AggregatedInventory/DWAvgTargetPrice)"
      - Change [AuctionUI.AllBidDownload.A_YYYEstimatedQuantity] to: "$AggregatedInventory/DWTotalQuantity"**
2. **Decision:** "BidData not empty?"
   - If [true] -> Move to: **Bid quantity 
Empty**
   - If [false] -> Move to: **Activity**
3. **Decision:** "Bid quantity 
Empty"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Bid quantity 
zero**
4. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload.A_YYYQuantityCap] to: "empty"**
5. **Decision:** "Bid Amount
Not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Update the **$undefined** (Object):
      - Change [AuctionUI.AllBidDownload.A_YYY] to: "'$0.00'"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
