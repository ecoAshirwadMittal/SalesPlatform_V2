# Microflow Analysis: IVK_ConstraintSaveAndNext

### Requirements (Inputs):
- **$MxConstraint** (A record of type: XLSReport.MxConstraint)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxXPath****
2. **Decision:** "Empty"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Retrieve
      - Store the result in a new variable called **$MxSheet****
4. **Search the Database for **XLSReport.MxReferenceHandling** using filter: { [XLSReport.MxReferenceHandling_MxSheet = $MxSheet] } (Call this list **$MxReferenceHandlingList**)**
5. **Run another process: "XLSReport.XPath_Validate"
      - Store the result in a new variable called **$XpathCommit****
6. **Decision:** "Xpath commit"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
7. **Permanently save **$undefined** to the database.**
8. **Run another process: "XLSReport.XPath_Delete_Subs"**
9. **Run another process: "XLSReport.SF_FindAttribute"
      - Store the result in a new variable called **$Attribute****
10. **Run another process: "XLSReport.SF_ParseDateType"
      - Store the result in a new variable called **$AttributeType****
11. **Update the **$undefined** (Object):
      - Change [XLSReport.MxConstraint.Attribute] to: "$Attribute/AttributeName"
      - Change [XLSReport.MxConstraint.AttributeType] to: "$AttributeType"
      - Change [XLSReport.MxConstraint.Summary] to: "$Attribute/CompleteName"
      - Change [XLSReport.MxConstraint.Constraint] to: "if $AttributeType = XLSReport.AttributeType.YesNo
then
	XLSReport.ConstraintType.Equal
else
	$MxConstraint/Constraint"**
12. **Close Form**
13. **Show Page**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
