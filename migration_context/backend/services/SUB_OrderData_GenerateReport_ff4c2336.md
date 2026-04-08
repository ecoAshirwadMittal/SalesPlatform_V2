# Microflow Analysis: SUB_OrderData_GenerateReport

### Requirements (Inputs):
- **$Template** (A record of type: XLSReport.MxTemplate)
- **$OrderDataDoc** (A record of type: EcoATM_PWS.OrderExcelDocument)
- **$FileName** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Java Action Call
      - Store the result in a new variable called **$Document**** ⚠️ *(This step has a safety catch if it fails)*
5. **Update the **$undefined** (Object):
      - Change [System.FileDocument.Name] to: "$FileName
"
      - **Save:** This change will be saved to the database immediately.**
6. **Download File**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
