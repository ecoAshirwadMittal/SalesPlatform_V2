# Microflow Analysis: IVK_Constraint_Lower

### Requirements (Inputs):
- **$MxConstraint** (A record of type: XLSReport.MxConstraint)
- **$MxSheet** (A record of type: XLSReport.MxSheet)

### Execution Steps:
1. **Search the Database for **XLSReport.MxConstraint** using filter: { [XLSReport.MxConstraint_MxSheet = $MxSheet]
[Sequence < $MxConstraint/Sequence] } (Call this list **$LowerMxConstraint**)**
2. **Decision:** "Found"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [XLSReport.MxConstraint.Sequence] to: "$LowerMxConstraint/Sequence + 1"
      - **Save:** This change will be saved to the database immediately.**
4. **Update the **$undefined** (Object):
      - Change [XLSReport.MxConstraint.Sequence] to: "$LowerMxConstraint/Sequence - 1"
      - **Save:** This change will be saved to the database immediately.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
