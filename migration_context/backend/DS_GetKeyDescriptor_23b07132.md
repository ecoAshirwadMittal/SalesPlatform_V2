# Microflow Analysis: DS_GetKeyDescriptor

### Requirements (Inputs):
- **$KeyInfo** (A record of type: SAML20.KeyInfo)

### Execution Steps:
1. **Search the Database for **SAML20.KeyDescriptor** using filter: { [SAML20.KeyDescriptor_KeyInfo = $KeyInfo] } (Call this list **$KeyDescriptorType**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
