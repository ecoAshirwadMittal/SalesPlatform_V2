# Microflow Analysis: IVK_SortingDelete

### Requirements (Inputs):
- **$MxSorting** (A record of type: XLSReport.MxSorting)

### Execution Steps:
1. **Search the Database for **XLSReport.MxSorting** using filter: { [XLSReport.MxSorting_MxSheet = $MxSorting/XLSReport.MxSorting_MxSheet]
[Sequence > $MxSorting/Sequence]
 } (Call this list **$MxSortingList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Delete**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
