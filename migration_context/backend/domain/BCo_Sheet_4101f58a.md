# Microflow Analysis: BCo_Sheet

### Requirements (Inputs):
- **$MxSheet** (A record of type: XLSReport.MxSheet)

### Execution Steps:
1. **Search the Database for **XLSReport.MxColumn** using filter: { [XLSReport.MxData_MxSheet = $MxSheet]
[DataAggregate] } (Call this list **$MxColumnList**)**
2. **Aggregate List
      - Store the result in a new variable called **$count****
3. **Update the **$undefined** (Object):
      - Change [XLSReport.MxSheet.FormLayout_GroupBy] to: "$count > 0"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
