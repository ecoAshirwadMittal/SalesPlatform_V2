# Microflow Detailed Specification: IVK_Column_New

### 📥 Inputs (Parameters)
- **$MxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$MxSheet/XLSReport.MxSheet_RowObject = empty`
   ➔ **If [false]:**
      1. **Create **XLSReport.MxColumn** (Result: **$NewMxColumn**)
      - Set **MxData_MxSheet** = `$MxSheet`**
      2. **Call Microflow **XLSReport.XPath_New****
      3. **Maps to Page: **XLSReport.MxColumn_NewEdit****
      4. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Show Message (Information): `Select first the row object, before you can create a column definition.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.