# Microflow Detailed Specification: IVK_SortingDelete

### 📥 Inputs (Parameters)
- **$MxSorting** (Type: XLSReport.MxSorting)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **XLSReport.MxSorting** Filter: `[XLSReport.MxSorting_MxSheet = $MxSorting/XLSReport.MxSorting_MxSheet] [Sequence > $MxSorting/Sequence]` (Result: **$MxSortingList**)**
2. 🔄 **LOOP:** For each **$HigherSorting** in **$MxSortingList**
   │ 1. **Update **$HigherSorting**
      - Set **Sequence** = `$HigherSorting/Sequence - 1`**
   └─ **End Loop**
3. **Commit/Save **$MxSortingList** to Database**
4. **Delete**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.