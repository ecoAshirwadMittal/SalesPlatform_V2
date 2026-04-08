# Microflow Analysis: ACT_Get_CellStyle

### Requirements (Inputs):
- **$MxData** (A record of type: XLSReport.MxData)
- **$Name** (A record of type: Object)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxSheet****
2. **Retrieve
      - Store the result in a new variable called **$MxTemplate****
3. **Retrieve
      - Store the result in a new variable called **$MxCellStyleList****
4. **Take the list **$MxCellStyleList**, perform a [FindByExpression] where: { $currentObject/Name = $Name }, and call the result **$NewMxCellStyle****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
