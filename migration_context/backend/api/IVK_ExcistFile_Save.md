# Microflow Detailed Specification: IVK_ExcistFile_Save

### 📥 Inputs (Parameters)
- **$ExcistExcel** (Type: XLSReport.CustomExcel)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$ExcistExcel/HasContents`
   ➔ **If [true]:**
      1. **Update **$ExcistExcel** (and Save to DB)**
      2. **Close current page/popup**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `Select a excel file to upload`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.