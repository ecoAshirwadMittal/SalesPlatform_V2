# Microflow Analysis: Sub_CreateColumnsFromTemplate

### Requirements (Inputs):
- **$Template** (A record of type: ExcelImporter.Template)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxObjectType****
2. **Create Variable**
3. **Search the Database for **MxModelReflection.MxObjectMember** using filter: { [MxModelReflection.MxObjectMember_MxObjectType = $MxObjectType][AttributeTypeEnum != $AutoNumberToBeIgnored] } (Call this list **$MemberList**)**
4. **Retrieve
      - Store the result in a new variable called **$ColumnList****
5. **Aggregate List
      - Store the result in a new variable called **$NextColNumber****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Permanently save **$undefined** to the database.**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
