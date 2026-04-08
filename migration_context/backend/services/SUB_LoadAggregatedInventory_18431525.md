# Microflow Analysis: SUB_LoadAggregatedInventory

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Retrieve
      - Store the result in a new variable called **$AggreegatedInventoryTotals****
3. **Retrieve
      - Store the result in a new variable called **$AggregateInventoryList****
4. **Delete**
5. **Delete**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Create Variable**
8. **Create Variable**
9. **Create Variable**
10. **Change Variable**
11. **Execute Database Query
      - Store the result in a new variable called **$AggreegatedInventory**** ⚠️ *(This step has a safety catch if it fails)*
12. **Run another process: "Custom_Logging.SUB_Log_Info"**
13. **Decision:** "Any more Results ?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
14. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
15. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
16. **Run another process: "Custom_Logging.SUB_Log_Info"**
17. **Change Variable**

**Conclusion:** This process sends back a [Void] result.
