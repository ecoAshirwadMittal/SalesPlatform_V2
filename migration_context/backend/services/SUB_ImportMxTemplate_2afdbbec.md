# Microflow Analysis: SUB_ImportMxTemplate

### Requirements (Inputs):
- **$CustomExcel** (A record of type: XLSReport.CustomExcel)

### Execution Steps:
1. **Decision:** "contents?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Import Xml**
3. **Close Form**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
