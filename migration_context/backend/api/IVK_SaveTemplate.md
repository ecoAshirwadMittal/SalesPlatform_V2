# Microflow Detailed Specification: IVK_SaveTemplate

### 📥 Inputs (Parameters)
- **$Template** (Type: ExcelImporter.Template)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **ExcelImporter.CleanupOldRefHandling** (Result: **$Variable**)**
2. **Call Microflow **ExcelImporter.ValidateTemplate** (Result: **$valid**)**
3. 🔀 **DECISION:** `$valid`
   ➔ **If [true]:**
      1. **Call Microflow **ExcelImporter.GetAddProperties** (Result: **$AddProperties**)**
      2. **Update **$AddProperties** (and Save to DB)**
      3. **Update **$Template** (and Save to DB)
      - Set **Status** = `ExcelImporter.Status.VALID`**
      4. **Close current page/popup**
      5. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **Update **$Template** (and Save to DB)
      - Set **Status** = `ExcelImporter.Status.INVALID`**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.