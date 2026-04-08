# Microflow Analysis: IVK_Column_Save

### Requirements (Inputs):
- **$MxColumn** (A record of type: XLSReport.MxColumn)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxSheet****
2. **Run another process: "XLSReport.SF_Column_Validate"
      - Store the result in a new variable called **$Commit****
3. **Decision:** "voldoet"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Column status good**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
