# Microflow Analysis: ACT_UploadPWSPricingFile

### Requirements (Inputs):
- **$PricingUpdateFile** (A record of type: EcoATM_PWS.PricingUpdateFile)
- **$MDMFuturePriceHelper** (A record of type: EcoATM_PWS.MDMFuturePriceHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Import Excel Data
      - Store the result in a new variable called **$PricingDataExcelImporterList****
5. **Take the list **$PricingDataExcelImporterList**, perform a [FilterByExpression] where: { $currentObject/New_List_Price != empty
and
$currentObject/New_Min_Price != empty
and
$currentObject/New_List_Price != ''
and
$currentObject/New_List_Price != '$'
and
$currentObject/New_List_Price != '$0'
and
$currentObject/New_List_Price != '0'
and
$currentObject/New_Min_Price != ''
and
$currentObject/New_Min_Price != '$'
and
$currentObject/New_Min_Price != '$0'
and
$currentObject/New_Min_Price != '0' }, and call the result **$PricingDataExcelImporterList_Filtered****
6. **Create Variable**
7. **Create Variable**
8. **Create Variable**
9. **Create Variable**
10. **Create Variable**
11. **Create List
      - Store the result in a new variable called **$PricingDataImportHelperList****
12. **Create List
      - Store the result in a new variable called **$DeviceList_ToBeUpdated****
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Decision:** "No File Errors?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
15. **Permanently save **$undefined** to the database.**
16. **Run another process: "EcoATM_PWS.ACT_SendPricingDataToSnowflake"**
17. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
18. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
