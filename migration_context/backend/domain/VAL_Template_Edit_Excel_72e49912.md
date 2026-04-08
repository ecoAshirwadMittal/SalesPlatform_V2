# Microflow Analysis: VAL_Template_Edit_Excel

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Is Name not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "Is DateTimePresentation not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
