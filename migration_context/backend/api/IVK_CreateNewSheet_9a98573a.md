# Microflow Analysis: IVK_CreateNewSheet

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxCellStyleList****
2. **Decision:** "Styles present"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Object
      - Store the result in a new variable called **$NewMxSheet****
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
