# Microflow Analysis: SUB_Import_XPathRecursion

### Requirements (Inputs):
- **$NewMxData** (A record of type: XLSReport.MxData)
- **$MxXPathList** (A record of type: XLSReport.MxXPath)
- **$ParentMxXPath** (A record of type: XLSReport.MxXPath)
- **$MxSorting** (A record of type: XLSReport.MxSorting)
- **$MxConstraint** (A record of type: XLSReport.MxConstraint)

### Execution Steps:
1. **Decision:** "niet leeg?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
