# Microflow Analysis: ACT_DeleteInventoryPlusBidsReportForWeek

### Requirements (Inputs):
- **$Week** (A record of type: AuctionUI.Week)

### Execution Steps:
1. **Search the Database for **AuctionUI.Inventory** using filter: { [(AuctionUI.Inventory_Week = $Week)] } (Call this list **$InventoryList_1**)**
2. **Aggregate List
      - Store the result in a new variable called **$TotalInventoryCount****
3. **Create Variable**
4. **Create Variable**
5. **Create Variable**
6. **Search the Database for **AuctionUI.Inventory** using filter: { [(AuctionUI.Inventory_Week = $Week)] } (Call this list **$InventoryList**)**
7. **Delete**
8. **Java Action Call
      - Store the result in a new variable called **$Variable****
9. **Change Variable**
10. **Decision:** "done?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
11. **Run another process: "AuctionUI.ACT_DeleteAggregatedInventoryForWeek"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
