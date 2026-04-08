# Microflow Analysis: SUB_LoadPWSInventory_Deposco

### Requirements (Inputs):
- **$FullInventorySync** (A record of type: Object)

### Execution Steps:
1. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
2. **Decision:** "Is FeatureFlagState?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Create Variable**
5. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
6. **Search the Database for **EcoATM_PWSIntegration.PWSInventory** using filter: { Show everything } (Call this list **$PWSInventory**)**
7. **Search the Database for **EcoATM_PWSIntegration.DeposcoConfig** using filter: { Show everything } (Call this list **$DeposcoConfig**)**
8. **Create Variable**
9. **Create Variable**
10. **Retrieve
      - Store the result in a new variable called **$DesposcoAPIsList****
11. **Take the list **$DesposcoAPIsList**, perform a [Find] where: { EcoATM_PWSIntegration.ENUM_DeposcoServices.Inventory }, and call the result **$DesposcoAPI****
12. **Create List
      - Store the result in a new variable called **$ItemInventoryItemList****
13. **Create Variable**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Update the **$undefined** (Object):
      - Change [EcoATM_PWSIntegration.DeposcoConfig.LastSyncTime] to: "$SyncBeginTime
"
      - **Save:** This change will be saved to the database immediately.**
16. **Create List
      - Store the result in a new variable called **$DeviceWithDeltas****
17. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { Show everything } (Call this list **$DeviceList**)**
18. **Create Variable**
19. **Create Variable**
20. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
21. **Aggregate List
      - Store the result in a new variable called **$CountOfDevices****
22. **Run another process: "Custom_Logging.SUB_Log_Info"**
23. **Run another process: "EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice"** ⚠️ *(This step has a safety catch if it fails)*
24. **Run another process: "EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters"** ⚠️ *(This step has a safety catch if it fails)*
25. **Run another process: "EcoATM_PWS.SUB_CleanupMetaData"** ⚠️ *(This step has a safety catch if it fails)*
26. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
27. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
