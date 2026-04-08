# Microflow Analysis: IVK_UploadExcistFile

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Decision:** "Excist"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Retrieve
      - Store the result in a new variable called **$ExcistExcel****
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
