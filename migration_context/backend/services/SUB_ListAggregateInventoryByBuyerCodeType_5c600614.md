# Microflow Analysis: SUB_ListAggregateInventoryByBuyerCodeType

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
2. **Decision:** "DW Buyer Code?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [
AuctionUI.AggregatedInventory_Week = $Auction/AuctionUI.Auction_Week and
DWTotalQuantity > 0
]
 } (Call this list **$AggregatedInventoryList_DataWipe**)**
4. **Run another process: "Custom_Logging.SUB_Log_EndTimer"
      - Store the result in a new variable called **$Log****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
