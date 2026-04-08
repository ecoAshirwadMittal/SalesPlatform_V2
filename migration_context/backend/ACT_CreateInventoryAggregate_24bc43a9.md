# Microflow Analysis: ACT_CreateInventoryAggregate

### Requirements (Inputs):
- **$Week** (A record of type: AuctionUI.Week)

### Execution Steps:
1. **Create Variable**
2. **Java Action Call
      - Store the result in a new variable called **$AgregatedInventory_Count_groupby****
3. **Create Variable**
4. **Java Action Call
      - Store the result in a new variable called **$AgregatedInventory_Count****
5. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [(AuctionUI.AggregatedInventory_Week = $Week)] } (Call this list **$ExistingAgregatedInventoryList**)**
6. **Delete**
7. **Create List
      - Store the result in a new variable called **$AgregatedInventoryList_BarcodeCount_Processed****
8. **Create List
      - Store the result in a new variable called **$AgregatedInventoryList****
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Create Variable**
11. **Take the list **$AgregatedInventory_Count**, perform a [Subtract], and call the result **$AggregatedInventory_Subtract****
12. **Take the list **$AgregatedInventory_Count_groupby**, perform a [Subtract], and call the result **$AggregatedInventory_Subtract_GroupBy_Processed****
13. **Take the list **$AggregatedInventory_Subtract_GroupBy_Processed**, perform a [Subtract], and call the result **$AggregatedInventory_Subtract_GroupBy_Processed_2****
14. **Java Action Call
      - Store the result in a new variable called **$AgregatedInventory_DataWipeCount****
15. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
16. **Permanently save **$undefined** to the database.**
17. **Delete**
18. **Delete**
19. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
