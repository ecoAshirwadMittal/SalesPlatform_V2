# Microflow Analysis: SUB_ImportMxStatic

### Requirements (Inputs):
- **$NewMxSheet** (A record of type: XLSReport.MxSheet)
- **$MxStatic** (A record of type: XLSReport.MxStatic)
- **$NewMxTemplate** (A record of type: XLSReport.MxTemplate)
- **$NewMxColumn** (A record of type: XLSReport.MxColumn)

### Execution Steps:
1. **Run another process: "XLSReport.StringToMxCellStyle"
      - Store the result in a new variable called **$MxCellStyle****
2. **Retrieve
      - Store the result in a new variable called **$MxObjectMember****
3. **Create Object
      - Store the result in a new variable called **$NewMxStatic****
4. **Search the Database for **XLSReport.MxXPath** using filter: { [XLSReport.MxXPath_MxData = $NewMxStatic] } (Call this list **$MxXPath**)**
5. **Run another process: "XLSReport.SUB_Import_XPathList"
      - Store the result in a new variable called **$Variable****
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
