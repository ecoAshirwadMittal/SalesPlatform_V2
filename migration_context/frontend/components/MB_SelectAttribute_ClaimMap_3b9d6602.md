# Microflow Analysis: MB_SelectAttribute_ClaimMap

### Requirements (Inputs):
- **$Attribute** (A record of type: SAML20.Attribute)
- **$ClaimMap** (A record of type: SAML20.ClaimMap)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [SAML20.ClaimMap_Attribute] to: "$Attribute"**
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
