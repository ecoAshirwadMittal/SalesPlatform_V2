# Microflow Detailed Specification: IVK_Sorting_Lower

### 📥 Inputs (Parameters)
- **$MxSorting** (Type: XLSReport.MxSorting)
- **$MxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxSorting** Filter: `[XLSReport.MxSorting_MxSheet = $MxSheet] [Sequence < $MxSorting/Sequence]` (Result: **$LowerMxSorting**)**
2. 🔀 **DECISION:** `$LowerMxSorting != empty`
   ➔ **If [true]:**
      1. **Update **$MxSorting** (and Save to DB)
      - Set **Sequence** = `$MxSorting/Sequence - 1`**
      2. **Update **$LowerMxSorting** (and Save to DB)
      - Set **Sequence** = `$LowerMxSorting/Sequence + 1`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.