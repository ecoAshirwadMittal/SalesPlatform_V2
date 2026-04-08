# Microflow Analysis: DS_Sorting_reference

### Requirements (Inputs):
- **$MxXpath** (A record of type: XLSReport.MxXPath)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxSorting****
2. **Search the Database for **MxModelReflection.MxObjectReference** using filter: { [MxModelReflection.MxObjectReference_MxObjectType/MxModelReflection.MxObjectType/XLSReport.MxSheet_RowObject = $MxSorting/XLSReport.MxSorting_MxSheet]
 } (Call this list **$MxObjectReferenceList**)**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
