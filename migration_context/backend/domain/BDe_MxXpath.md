# Microflow Detailed Specification: BDe_MxXpath

### 📥 Inputs (Parameters)
- **$MxXPath** (Type: XLSReport.MxXPath)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$MxXPath/XLSReport.MxXPath_ParentMxXPath != empty`
   ➔ **If [true]:**
      1. **Retrieve related **MxXPath_ParentMxXPath** via Association from **$MxXPath** (Result: **$ChildXPath**)**
      2. **Delete**
      3. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.