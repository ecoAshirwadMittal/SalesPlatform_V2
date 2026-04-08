# Microflow Analysis: IVK_Column_New

### Requirements (Inputs):
- **$MxSheet** (A record of type: XLSReport.MxSheet)

### Execution Steps:
1. **Decision:** "Check condition"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
2. **Create Object
      - Store the result in a new variable called **$NewMxColumn****
3. **Run another process: "XLSReport.XPath_New"**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
