# Microflow Analysis: ACT_ExportRMAExcelFile

### Requirements (Inputs):
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Object
      - Store the result in a new variable called **$NewRMAExcelDocument****
4. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'RMAExport']
 } (Call this list **$MxTemplate**)**
5. **Create Variable**
6. **Retrieve
      - Store the result in a new variable called **$RMAList****
7. **Decision:** "Has RMA Requests?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
8. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
9. **Permanently save **$undefined** to the database.**
10. **Run another process: "EcoATM_RMA.SUB_RMARequests_GenerateReport"** ⚠️ *(This step has a safety catch if it fails)*
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
