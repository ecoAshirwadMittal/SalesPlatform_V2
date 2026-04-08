# Microflow Analysis: IVK_ExcistFile_Save

### Requirements (Inputs):
- **$ExcistExcel** (A record of type: XLSReport.CustomExcel)

### Execution Steps:
1. **Decision:** "Has Contents"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - **Save:** This change will be saved to the database immediately.**
3. **Close Form**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
