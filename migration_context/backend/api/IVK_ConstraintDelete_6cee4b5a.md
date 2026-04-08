# Microflow Analysis: IVK_ConstraintDelete

### Requirements (Inputs):
- **$MxConstraint** (A record of type: XLSReport.MxConstraint)

### Execution Steps:
1. **Search the Database for **XLSReport.MxConstraint** using filter: { [XLSReport.MxConstraint_MxSheet = $MxConstraint/XLSReport.MxConstraint_MxSheet]
[Sequence > $MxConstraint/Sequence] } (Call this list **$MxConstraintList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Delete**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
