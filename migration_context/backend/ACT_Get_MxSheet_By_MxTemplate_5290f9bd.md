# Microflow Analysis: ACT_Get_MxSheet_By_MxTemplate

### Requirements (Inputs):
- **$MxTemplate** (A record of type: XLSReport.MxTemplate)
- **$Name** (A record of type: Object)
- **$Sequence** (A record of type: Object)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxSheetList****
2. **Take the list **$MxSheetList**, perform a [FindByExpression] where: { $currentObject/Name = $Name and $currentObject/Sequence = $Sequence }, and call the result **$NewMxSheet****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
