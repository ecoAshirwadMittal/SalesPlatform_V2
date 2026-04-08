# Microflow Detailed Specification: SUB_Import_XPathList

### 📥 Inputs (Parameters)
- **$NewMxData** (Type: XLSReport.MxData)
- **$MxSorting** (Type: XLSReport.MxSorting)
- **$MxConstraint** (Type: XLSReport.MxConstraint)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxXPath** Filter: `[XLSReport.MxXPath_MxData = $NewMxData]` (Result: **$MxXPathList**)**
2. **Call Microflow **XLSReport.SUB_Import_XPathRecursion** (Result: **$Variable**)**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.