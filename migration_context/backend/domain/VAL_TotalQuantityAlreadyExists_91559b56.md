# Microflow Analysis: VAL_TotalQuantityAlreadyExists

### Requirements (Inputs):
- **$BidDataTotalQuantityConfig_current** (A record of type: AuctionUI.BidDataTotalQuantityConfig)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **AuctionUI.BidDataTotalQuantityConfig** using filter: { Show everything } (Call this list **$BidDataTotalQuantityConfigList**)**
3. **Change List**
4. **Decision:** "list not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Take the list **$BidDataTotalQuantityConfigList**, perform a [FindByExpression] where: { $BidDataTotalQuantityConfig_current/EcoID=$currentObject/EcoID
and
$BidDataTotalQuantityConfig_current/Grade=$currentObject/Grade }, and call the result **$BidDataTotalQuantityConfigExists****
6. **Decision:** "eco id does not exist?
"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
7. **Change Variable**
8. **Validation Feedback**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
