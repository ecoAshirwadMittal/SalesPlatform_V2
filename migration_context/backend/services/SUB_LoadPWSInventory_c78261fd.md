# Microflow Analysis: SUB_LoadPWSInventory

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "EcoATM_PWS.DS_GetOrCreateMDMFuturePriceHelper"
      - Store the result in a new variable called **$MDMFuturePriceHelper****
5. **Create Variable**
6. **Create Variable**
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [changedDate < $StartTime]
 } (Call this list **$DeviceList_Inactive**)**
9. **Create List
      - Store the result in a new variable called **$BuyerOfferItemList****
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Permanently save **$undefined** to the database.**
12. **Delete**
13. **Run another process: "EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters"** ⚠️ *(This step has a safety catch if it fails)*
14. **Run another process: "EcoATM_PWS.SUB_CleanupMetaData"** ⚠️ *(This step has a safety catch if it fails)*
15. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
