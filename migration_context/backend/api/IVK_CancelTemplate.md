# Microflow Detailed Specification: IVK_CancelTemplate

### 📥 Inputs (Parameters)
- **$Template** (Type: ExcelImporter.Template)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `isNew( $Template )`
   ➔ **If [false]:**
      1. **Rollback**
      2. **Call Microflow **ExcelImporter.ValidateTemplate** (Result: **$valid**)**
      3. **Close current page/popup**
      4. 🔀 **DECISION:** `$valid`
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Rollback**
      2. **Close current page/popup**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.