# Microflow Analysis: IVK_ConstraintSave

### Requirements (Inputs):
- **$MxConstraint** (A record of type: XLSReport.MxConstraint)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Decision:** "Attribute Type"
   - If [(empty)] -> Move to: **Finish**
   - If [Decimal] -> Move to: **Activity**
   - If [Text] -> Move to: **Activity**
   - If [Number] -> Move to: **Activity**
   - If [Date] -> Move to: **Date given**
   - If [YesNo] -> Move to: **Propper contraint**
4. **Validation Feedback**
5. **Change Variable**
6. **Decision:** "AndOr set"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
7. **Decision:** "Commit"
   - If [true] -> Move to: **Sequence set**
   - If [false] -> Move to: **Finish**
8. **Decision:** "Sequence set"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
9. **Update the **$undefined** (Object):
      - Change [XLSReport.MxConstraint.Summary] to: "$MxConstraint/Summary+' '+getCaption($MxConstraint/Constraint)+' '+$Value"
      - **Save:** This change will be saved to the database immediately.**
10. **Close Form**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
