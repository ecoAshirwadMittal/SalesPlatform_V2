# Microflow Analysis: ACT_PricingDataExport_ExportExcel

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [IsActive]
 } (Call this list **$DeviceList**)**
5. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'PWSPricing']
 } (Call this list **$MxTemplate**)**
6. **Create Object
      - Store the result in a new variable called **$NewPricingExcelDocument****
7. **Create List
      - Store the result in a new variable called **$PricingDataExportList****
8. **Create Variable**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Permanently save **$undefined** to the database.**
11. **Create Variable**
12. **Run another process: "EcoATM_PWS.SUB_PricingData_GenerateReport"** ⚠️ *(This step has a safety catch if it fails)*
13. **Delete**
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
