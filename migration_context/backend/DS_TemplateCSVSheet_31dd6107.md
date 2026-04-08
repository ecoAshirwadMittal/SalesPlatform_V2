# Microflow Analysis: DS_TemplateCSVSheet

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Search the Database for **XLSReport.MxSheet** using filter: { [XLSReport.MxSheet_Template = $MxTemplate] } (Call this list **$MxSheet**)**
2. **Decision:** "Found"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$NewMxSheet****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
