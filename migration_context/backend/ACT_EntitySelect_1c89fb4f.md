# Microflow Analysis: ACT_EntitySelect

### Requirements (Inputs):
- **$Entity** (A record of type: DeepLink.Entity)
- **$DeepLink** (A record of type: DeepLink.DeepLink)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [DeepLink.DeepLink_Entity] to: "$Entity"
      - Change [DeepLink.DeepLink.ObjectType] to: "$Entity/Name"**
2. **Close Form**
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
