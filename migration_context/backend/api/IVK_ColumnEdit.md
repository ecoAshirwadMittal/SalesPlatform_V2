# Microflow Detailed Specification: IVK_ColumnEdit

### 📥 Inputs (Parameters)
- **$Column** (Type: ExcelImporter.Column)
- **$EnclosingContext** (Type: ExcelImporter.Template)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EnclosingContext/ExcelImporter.Template_MxObjectType != empty`
   ➔ **If [true]:**
      1. **Maps to Page: **ExcelImporter.Column_NewEdit****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `A MetaObject must be selected before a column can be created or changed.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.