# Microflow Analysis: SUB_GetOrCreateModelName

### Requirements (Inputs):
- **$ModelNameList** (A record of type: EcoATM_MDM.ModelName)
- **$ModelNameText** (A record of type: Object)
- **$enum_BuyerCodeType** (A record of type: Object)

### Execution Steps:
1. **Take the list **$ModelNameList**, perform a [FindByExpression] where: { $currentObject/ModelName = $ModelNameText
and
$currentObject/BuyercodeType = $enum_BuyerCodeType }, and call the result **$Existing****
2. **Decision:** "Found ?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
