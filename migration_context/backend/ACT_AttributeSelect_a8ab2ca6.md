# Microflow Analysis: ACT_AttributeSelect

### Requirements (Inputs):
- **$Attribute** (A record of type: DeepLink.Attribute)
- **$DeepLink** (A record of type: DeepLink.DeepLink)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [DeepLink.DeepLink_Attribute] to: "$Attribute"
      - Change [DeepLink.DeepLink.ObjectAttribute] to: "$Attribute/Name"**
2. **Close Form**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
