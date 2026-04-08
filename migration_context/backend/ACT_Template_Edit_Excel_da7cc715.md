# Microflow Analysis: ACT_Template_Edit_Excel

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Run another process: "XLSReport.VAL_Template_Edit_Excel"
      - Store the result in a new variable called **$IsValid****
2. **Decision:** "IsValid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Permanently save **$undefined** to the database.**
4. **Close Form**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
