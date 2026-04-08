# Microflow Analysis: ACT_GetOrCreate_MxSheet

### Requirements (Inputs):
- **$MappingParent** (A record of type: XLSReport.MxCellStyle)
- **$Name** (A record of type: Object)
- **$Sequence** (A record of type: Object)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxTemplate****
2. **Retrieve
      - Store the result in a new variable called **$MxSheetList****
3. **Take the list **$MxSheetList**, perform a [FindByExpression] where: { $currentObject/Name = $Name and $currentObject/Sequence = $Sequence }, and call the result **$NewMxSheet****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
