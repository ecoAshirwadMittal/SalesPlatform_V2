# Microflow Analysis: DS_StaticInputMember

### Requirements (Inputs):
- **$MxStatic** (A record of type: XLSReport.MxStatic)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxSheet****
2. **Retrieve
      - Store the result in a new variable called **$MxTemplate****
3. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $MxTemplate/XLSReport.MxTemplate_InputObject] } (Call this list **$MxObjectMemberList**)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
