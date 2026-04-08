# Microflow Analysis: IVK_TemplateEdit

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Decision:** "Check condition"
   - If [XLS] -> Move to: **Finish**
   - If [(empty)] -> Move to: **Finish**
   - If [XLSX] -> Move to: **Finish**
   - If [CSV] -> Move to: **Activity**
   - If [XLSM] -> Move to: **Finish**
2. **Show Page**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
