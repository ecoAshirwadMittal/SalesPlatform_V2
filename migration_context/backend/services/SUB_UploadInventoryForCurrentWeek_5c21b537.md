# Microflow Analysis: SUB_UploadInventoryForCurrentWeek

### Execution Steps:
1. **Search the Database for **EcoATM_MDM.Week** using filter: { [WeekEndDateTime < '[%CurrentDateTime%]']
 } (Call this list **$Week**)**
2. **Run another process: "AuctionUI.SUB_LoadAggregatedInventoryTotals"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
