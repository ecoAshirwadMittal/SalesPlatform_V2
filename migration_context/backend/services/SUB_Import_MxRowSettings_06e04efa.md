# Microflow Analysis: SUB_Import_MxRowSettings

### Requirements (Inputs):
- **$MxSheet** (A record of type: XLSReport.MxSheet)
- **$NewMxSheet** (A record of type: XLSReport.MxSheet)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxRowSettingsList****
2. **Create List
      - Store the result in a new variable called **$MxRowSettingsCommitList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
