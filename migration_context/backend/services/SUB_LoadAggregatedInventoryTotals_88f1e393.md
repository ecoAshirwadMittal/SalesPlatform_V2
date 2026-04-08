# Microflow Analysis: SUB_LoadAggregatedInventoryTotals

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Decision:** "scheduling auction?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Execute Database Query
      - Store the result in a new variable called **$MaxUploadTimeAggreegatedInventory**** ⚠️ *(This step has a safety catch if it fails)*
4. **Take the list **$MaxUploadTimeAggreegatedInventory**, perform a [Head], and call the result **$NewMaxUploadTimeAggreegatedInventory****
5. **Decision:** "$NewMaxUploadTimeAggreegatedInventory !=empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Create Variable**
7. **Retrieve
      - Store the result in a new variable called **$AggreegatedInventoryTotal_2****
8. **Decision:** "temp"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **$AggreegatedInventoryTotalLegacy_2=empty **
9. **Run another process: "AuctionUI.SUB_LoadAggregatedInventory"**
10. **Run another process: "AuctionUI.SUB_BuildAggregatedInventoryFilters"**
11. **Search the Database for **AuctionUI.AggregatedInventory** using filter: { [DeviceId = 'Total']
[AuctionUI.AggregatedInventory_Week = $Week]

 } (Call this list **$AggreegatedInventoryTotal**)**
12. **Run another process: "AuctionUI.SUB_GetOrCreateAgrregateTotals"
      - Store the result in a new variable called **$AggreegatedInventoryTotals****
13. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggreegatedInventoryTotals.MaxUploadTime] to: "$MaxDateTime
"
      - **Save:** This change will be saved to the database immediately.**
14. **Delete**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
