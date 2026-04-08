# Microflow Analysis: DS_GetOrCreateAggregatedInventoryHelper

### Execution Steps:
1. **Search the Database for **Administration.Account** using filter: { [id = $currentUser]
 } (Call this list **$Account**)**
2. **Retrieve
      - Store the result in a new variable called **$AggInventoryHelper****
3. **Search the Database for **AuctionUI.BidDataTotalQuantityConfig** using filter: { Show everything } (Call this list **$BidDataTotalQuantityConfigList**)**
4. **Decision:** "AgreegateInventoryHelper exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggInventoryHelper.isTotalQuantityModified] to: "if $BidDataTotalQuantityConfigList!=empty
then true
else false
"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
