# Microflow Analysis: IVK_ColumnLower

### Requirements (Inputs):
- **$MxColumn** (A record of type: XLSReport.MxColumn)

### Execution Steps:
1. **Search the Database for **XLSReport.MxColumn** using filter: { [XLSReport.MxData_MxSheet = $MxColumn/XLSReport.MxData_MxSheet]
[ColumnNumber < $MxColumn/ColumnNumber] } (Call this list **$LowerMxColumn**)**
2. **Decision:** "Found"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [XLSReport.MxColumn.ColumnNumber] to: "$LowerMxColumn/ColumnNumber + 1"
      - **Save:** This change will be saved to the database immediately.**
4. **Update the **$undefined** (Object):
      - Change [XLSReport.MxColumn.ColumnNumber] to: "$MxColumn/ColumnNumber - 1"
      - **Save:** This change will be saved to the database immediately.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
