# Microflow Analysis: SUB_Import_MxSheet

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)
- **$NewMxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxSheetList****
2. **Create List
      - Store the result in a new variable called **$MxSheetCommitList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
