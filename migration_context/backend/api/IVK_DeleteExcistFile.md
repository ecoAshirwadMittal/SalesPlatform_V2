# Microflow Detailed Specification: IVK_DeleteExcistFile

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxTemplate_CustomExcel** via Association from **$MxTemplate** (Result: **$ExcistExcel**)**
2. 🔀 **DECISION:** `$ExcistExcel != empty`
   ➔ **If [true]:**
      1. **Update **$MxTemplate** (and Save to DB)
      - Set **MxTemplate_CustomExcel** = `empty`**
      2. **Delete**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.