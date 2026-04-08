# Microflow Analysis: IVK_DeleteColumn

### Requirements (Inputs):
- **$MxColumn** (A record of type: XLSReport.MxColumn)

### Execution Steps:
1. **Search the Database for **XLSReport.MxColumn** using filter: { [XLSReport.MxData_MxSheet = $MxColumn/XLSReport.MxData_MxSheet]
[ColumnNumber > $MxColumn/ColumnNumber] } (Call this list **$MxColumnList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Delete**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
