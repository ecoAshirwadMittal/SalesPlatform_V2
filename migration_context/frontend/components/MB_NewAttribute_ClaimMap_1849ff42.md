# Microflow Analysis: MB_NewAttribute_ClaimMap

### Requirements (Inputs):
- **$ClaimMap** (A record of type: SAML20.ClaimMap)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SSOConfiguration****
2. **Create Object
      - Store the result in a new variable called **$NewAttribute****
3. **Update the **$undefined** (Object):
      - Change [SAML20.ClaimMap_Attribute] to: "$NewAttribute"**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
