# Microflow Analysis: SUB_Import_Mxtemplate

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)
- **$CustomExcel** (A record of type: XLSReport.CustomExcel)

### Execution Steps:
1. **Search the Database for **MxModelReflection.MxObjectType** using filter: { [XLSReport.MxTemplate_InputObject = $MxTemplate] } (Call this list **$MxObjectType**)**
2. **Create Object
      - Store the result in a new variable called **$NewMxTemplate****
3. **Run another process: "XLSReport.ImportMxCellStyle"
      - Store the result in a new variable called **$Variable****
4. **Run another process: "XLSReport.SUB_Import_MxSheet"**
5. **Run another process: "XLSReport.SUB_Import_CustomExcel"**
6. **Permanently save **$undefined** to the database.**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
