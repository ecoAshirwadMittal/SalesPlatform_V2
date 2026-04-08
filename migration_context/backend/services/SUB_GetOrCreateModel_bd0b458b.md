# Microflow Analysis: SUB_GetOrCreateModel

### Requirements (Inputs):
- **$ModelList** (A record of type: EcoATM_MDM.Model)
- **$ModelText** (A record of type: Object)
- **$enum_BuyerCodeType** (A record of type: Object)

### Execution Steps:
1. **Take the list **$ModelList**, perform a [FindByExpression] where: { $currentObject/Model = $ModelText
and
$currentObject/BuyercodeType = $enum_BuyerCodeType }, and call the result **$Existing****
2. **Decision:** "Found ?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
