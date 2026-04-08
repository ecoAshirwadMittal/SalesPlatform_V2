# Microflow Analysis: ACT_DownloadRMAFile

### Requirements (Inputs):
- **$RMAUiHelper** (A record of type: EcoATM_RMA.RMAUiHelper)
- **$RMAMasterHelper** (A record of type: EcoATM_RMA.RMAMasterHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Object
      - Store the result in a new variable called **$NewRMAExcelDocument****
4. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'RMAReturns']
 } (Call this list **$MxTemplate**)**
5. **Create Variable**
6. **Retrieve
      - Store the result in a new variable called **$RMAStatusList****
7. **Run another process: "EcoATM_RMA.DS_GetRMAsByStatus"
      - Store the result in a new variable called **$RMAList****
8. **Decision:** "Has RMAs?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Permanently save **$undefined** to the database.**
11. **Run another process: "EcoATM_RMA.SUB_RMARequests_GenerateReport"** ⚠️ *(This step has a safety catch if it fails)*
12. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
