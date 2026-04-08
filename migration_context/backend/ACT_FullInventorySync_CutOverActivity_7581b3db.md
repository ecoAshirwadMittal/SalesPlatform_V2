# Microflow Analysis: ACT_FullInventorySync_CutOverActivity

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
8. **Java Action Call
      - Store the result in a new variable called **$TruncateTempData****
9. **Create Variable**
10. **Create Variable**
11. **Retrieve
      - Store the result in a new variable called **$DesposcoAPIsList****
12. **Take the list **$DesposcoAPIsList**, perform a [Find] where: { EcoATM_PWSIntegration.ENUM_DeposcoServices.Inventory }, and call the result **$DesposcoAPI****
13. **Create List
      - Store the result in a new variable called **$ItemInventoryItemList****
14. **Create List
      - Store the result in a new variable called **$Facility****
15. **Create List
      - Store the result in a new variable called **$InventoryTest****
16. **Create Variable**
17. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
18. **Permanently save **$undefined** to the database.**
19. **Update the **$undefined** (Object):
      - Change [EcoATM_PWSIntegration.DeposcoConfig.LastSyncTime] to: "$SyncBeginTime
"
      - **Save:** This change will be saved to the database immediately.**
20. **Java Action Call
      - Store the result in a new variable called **$ForceCommit****
21. **Create Variable**
22. **Java Action Call
      - Store the result in a new variable called **$InsertReport****
23. **Search the Database for **EcoATM_PWSIntegration.DeposcoConfig** using filter: { Show everything } (Call this list **$DeposcoConfig_Updated**)**
24. **Create Variable**
25. **Import Xml**
26. **Take the list **$SyncSKUDetails**, perform a [Find] where: { 'PWS00000106992' }, and call the result **$SKUSyncDetail****
27. **Create Object
      - Store the result in a new variable called **$NewPWSInventorySyncReport****
28. **Create Variable**
29. **Java Action Call
      - Store the result in a new variable called **$UpdateSKUs****
30. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [LastSyncTime=$SyncBeginTime]
 } (Call this list **$DeviceWithDeltas**)**
31. **Aggregate List
      - Store the result in a new variable called **$CountOfDevices****
32. **Run another process: "Custom_Logging.SUB_Log_Info"**
33. **Run another process: "EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice"** ⚠️ *(This step has a safety catch if it fails)*
34. **Run another process: "EcoATM_PWS.SUB_CleanupMetaData"** ⚠️ *(This step has a safety catch if it fails)*
35. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
36. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
