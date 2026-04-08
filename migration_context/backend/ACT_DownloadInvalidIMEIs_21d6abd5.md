# Microflow Analysis: ACT_DownloadInvalidIMEIs

### Requirements (Inputs):
- **$InvalidRMAItems** (A record of type: EcoATM_RMA.InvalidRMAItem_UiHelper)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Object
      - Store the result in a new variable called **$NewInvalidRMAFile****
4. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'InvalidIMEIs']
 } (Call this list **$MxTemplate**)**
5. **Create Variable**
6. **Retrieve
      - Store the result in a new variable called **$InvalidIMEI_ExportHelperList****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Run another process: "EcoATM_RMA.SUB_InvalidRMA_GenerateReport"** ⚠️ *(This step has a safety catch if it fails)*
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
