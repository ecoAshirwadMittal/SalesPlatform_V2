# Microflow Analysis: SUB_Import_XPathList

### Requirements (Inputs):
- **$NewMxData** (A record of type: XLSReport.MxData)
- **$MxSorting** (A record of type: XLSReport.MxSorting)
- **$MxConstraint** (A record of type: XLSReport.MxConstraint)

### Execution Steps:
1. **Search the Database for **XLSReport.MxXPath** using filter: { [XLSReport.MxXPath_MxData = $NewMxData] } (Call this list **$MxXPathList**)**
2. **Run another process: "XLSReport.SUB_Import_XPathRecursion"
      - Store the result in a new variable called **$Variable****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
