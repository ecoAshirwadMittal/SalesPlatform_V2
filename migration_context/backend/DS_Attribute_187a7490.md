# Microflow Analysis: DS_Attribute

### Requirements (Inputs):
- **$MxXPath** (A record of type: XLSReport.MxXPath)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$ParentMxXPath****
2. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $ParentMxXPath/XLSReport.MxXPath_MxObjectType] } (Call this list **$MxObjectMemberList**)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
