# Microflow Detailed Specification: IVK_ColumnLower

### 📥 Inputs (Parameters)
- **$MxColumn** (Type: XLSReport.MxColumn)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxColumn** Filter: `[XLSReport.MxData_MxSheet = $MxColumn/XLSReport.MxData_MxSheet] [ColumnNumber < $MxColumn/ColumnNumber]` (Result: **$LowerMxColumn**)**
2. 🔀 **DECISION:** `$LowerMxColumn != empty`
   ➔ **If [true]:**
      1. **Update **$LowerMxColumn** (and Save to DB)
      - Set **ColumnNumber** = `$LowerMxColumn/ColumnNumber + 1`**
      2. **Update **$MxColumn** (and Save to DB)
      - Set **ColumnNumber** = `$MxColumn/ColumnNumber - 1`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.