# Microflow Analysis: IVK_DeleteExcistFile

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$ExcistExcel****
2. **Decision:** "Found"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [XLSReport.MxTemplate_CustomExcel] to: "empty"
      - **Save:** This change will be saved to the database immediately.**
4. **Delete**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
