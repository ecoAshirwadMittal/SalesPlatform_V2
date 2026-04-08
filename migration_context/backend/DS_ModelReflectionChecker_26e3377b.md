# Microflow Analysis: DS_ModelReflectionChecker

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Search the Database for **MxModelReflection.MxObjectType** using filter: { Show everything } (Call this list **$NewMxObjectType**)**
2. **Create Object
      - Store the result in a new variable called **$NewModelReflectionChecker****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
