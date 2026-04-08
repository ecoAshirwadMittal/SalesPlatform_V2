# Microflow Analysis: ACT_UpdateTargetPrice

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$MaxLotBid** (A record of type: AuctionUI.MaxLotBid)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
3. **Retrieve
      - Store the result in a new variable called **$Auction****
4. **Retrieve
      - Store the result in a new variable called **$Week****
5. **Search the Database for **EcoATM_PO.PurchaseOrder** using filter: { [EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week/WeekID <= $Week/WeekID
and EcoATM_PO.PurchaseOrder_Week_To/EcoATM_MDM.Week/WeekID >= $Week/WeekID] } (Call this list **$PurchaseOrderForWeek**)**
6. **Create Variable**
7. **Create List
      - Store the result in a new variable called **$UpdatedAggregatedInventoryList****
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Permanently save **$undefined** to the database.**
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
