# Microflow Analysis: SUB_LoadPWSInventory_Snowflake

### Execution Steps:
1. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
2. **Decision:** "Is FeatureFlagState?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Create Variable**
5. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
6. **Create Variable**
7. **Java Action Call
      - Store the result in a new variable called **$IsSuccess****
8. **Create Variable**
9. **Create Variable**
10. **Create Variable**
11. **Create Variable**
12. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
13. **Run another process: "Custom_Logging.SUB_Log_Info"**
14. **Java Action Call
      - Store the result in a new variable called **$updateSuccess****
15. **Run another process: "Custom_Logging.SUB_Log_Info"**
16. **Java Action Call
      - Store the result in a new variable called **$newDevicesSuccess****
17. **Search the Database for **EcoATM_PWSMDM.DeviceTemp** using filter: { [isNew]
 } (Call this list **$DeviceTempList**)**
18. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
19. **Create List
      - Store the result in a new variable called **$ToCommitDeviceList****
20. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
21. **Aggregate List
      - Store the result in a new variable called **$NewDevicesCount****
22. **Run another process: "Custom_Logging.SUB_Log_Info"**
23. **Permanently save **$undefined** to the database.**
24. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [LastUpdateDate < $StartTime]
 } (Call this list **$DeviceList_Inactive**)**
25. **Decision:** "exist?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Submicroflow**
26. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [IsActive] } (Call this list **$DeviceList**)**
27. **Run another process: "EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice"** ⚠️ *(This step has a safety catch if it fails)*
28. **Run another process: "EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters"** ⚠️ *(This step has a safety catch if it fails)*
29. **Run another process: "EcoATM_PWS.SUB_CleanupMetaData"** ⚠️ *(This step has a safety catch if it fails)*
30. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
31. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
