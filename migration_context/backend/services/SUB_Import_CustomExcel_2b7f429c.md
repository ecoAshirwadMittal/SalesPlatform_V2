# Microflow Analysis: SUB_Import_CustomExcel

### Requirements (Inputs):
- **$CustomExcel_2** (A record of type: XLSReport.CustomExcel)
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Decision:** "customExcel?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Update the **$undefined** (Object):
      - Change [XLSReport.MxTemplate_CustomExcel] to: "$CustomExcel_2"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
