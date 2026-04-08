# Microflow Analysis: IVK_TemplateSaveAndNext

### Requirements (Inputs):
- **$Template** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Name"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
3. **Decision:** "Document"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
4. **Decision:** "Good"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Update the **$undefined** (Object):
      - **Save:** This change will be saved to the database immediately.**
6. **Decision:** "Excel"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
7. **Create Object
      - Store the result in a new variable called **$NewMxCellStyle****
8. **Close Form**
9. **Run another process: "XLSReport.IVK_TemplateEdit"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
