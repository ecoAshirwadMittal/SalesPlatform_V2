# Microflow Analysis: IVK_Sorting_Higher

### Requirements (Inputs):
- **$MxSorting** (A record of type: XLSReport.MxSorting)
- **$MxSheet** (A record of type: XLSReport.MxSheet)

### Execution Steps:
1. **Search the Database for **XLSReport.MxSorting** using filter: { [XLSReport.MxSorting_MxSheet = $MxSheet]
[Sequence > $MxSorting/Sequence] } (Call this list **$HigherMxSorting**)**
2. **Decision:** "Found"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [XLSReport.MxSorting.Sequence] to: "$MxSorting/Sequence + 1"
      - **Save:** This change will be saved to the database immediately.**
4. **Update the **$undefined** (Object):
      - Change [XLSReport.MxSorting.Sequence] to: "$HigherMxSorting/Sequence - 1"
      - **Save:** This change will be saved to the database immediately.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
