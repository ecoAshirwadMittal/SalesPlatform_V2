# Microflow Analysis: SUB_GetOrCreateBrand

### Requirements (Inputs):
- **$BrandList** (A record of type: EcoATM_MDM.Brand)
- **$BrandText** (A record of type: Object)
- **$enum_BuyerCodeType** (A record of type: Object)

### Execution Steps:
1. **Take the list **$BrandList**, perform a [FindByExpression] where: { $currentObject/Brand = $BrandText
and
$currentObject/BuyercodeType = $enum_BuyerCodeType }, and call the result **$ExistingBrand****
2. **Decision:** "Found ?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
