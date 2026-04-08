# Microflow Analysis: DS_First_Attribute

### Requirements (Inputs):
- **$MxXpath** (A record of type: XLSReport.MxXPath)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxData****
2. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType/MxModelReflection.MxObjectType/XLSReport.MxSheet_RowObject = $MxData/XLSReport.MxData_MxSheet] } (Call this list **$MxObjectMemberList**)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
