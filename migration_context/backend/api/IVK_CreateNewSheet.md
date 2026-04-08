# Microflow Detailed Specification: IVK_CreateNewSheet

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxCellStyle_Template** via Association from **$MxTemplate** (Result: **$MxCellStyleList**)**
2. 🔀 **DECISION:** `$MxCellStyleList != empty`
   ➔ **If [true]:**
      1. **Create **XLSReport.MxSheet** (Result: **$NewMxSheet**)
      - Set **MxSheet_Template** = `$MxTemplate`**
      2. **Maps to Page: **XLSReport.MxSheet_NewEdit****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Before creating a sheet you must first create styles, those must be used in the sheet.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.