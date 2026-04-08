# Microflow Analysis: SUB_Import_MxColumn

### Requirements (Inputs):
- **$NewMxSheet** (A record of type: XLSReport.MxSheet)
- **$MxColumn** (A record of type: XLSReport.MxColumn)
- **$NewMxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Run another process: "XLSReport.StringToMxCellStyle"
      - Store the result in a new variable called **$MxCellStyle****
2. **Create Object
      - Store the result in a new variable called **$NewMxColumn****
3. **Run another process: "XLSReport.SUB_Import_XPathList"
      - Store the result in a new variable called **$Variable****
4. **Retrieve
      - Store the result in a new variable called **$MxStaticList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
