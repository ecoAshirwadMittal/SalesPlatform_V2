# Microflow Detailed Specification: IVK_ColumnHigher

### 📥 Inputs (Parameters)
- **$MxColumn** (Type: XLSReport.MxColumn)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxColumn** Filter: `[XLSReport.MxData_MxSheet = $MxColumn/XLSReport.MxData_MxSheet] [ColumnNumber > $MxColumn/ColumnNumber]` (Result: **$HigherMxColumn**)**
2. 🔀 **DECISION:** `$HigherMxColumn != empty`
   ➔ **If [true]:**
      1. **Update **$HigherMxColumn** (and Save to DB)
      - Set **ColumnNumber** = `$HigherMxColumn/ColumnNumber - 1`**
      2. **Update **$MxColumn** (and Save to DB)
      - Set **ColumnNumber** = `$MxColumn/ColumnNumber + 1`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.