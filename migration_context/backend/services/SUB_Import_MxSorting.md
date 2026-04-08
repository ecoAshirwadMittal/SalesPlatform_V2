# Microflow Detailed Specification: SUB_Import_MxSorting

### 📥 Inputs (Parameters)
- **$MxSheet** (Type: XLSReport.MxSheet)
- **$NewMxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxSorting_MxSheet** via Association from **$MxSheet** (Result: **$MxSortingList**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorMxSorting** in **$MxSortingList**
   │ 1. **Create **XLSReport.MxSorting** (Result: **$NewMxSorting**)
      - Set **Sequence** = `$IteratorMxSorting/Sequence`
      - Set **Summary** = `$IteratorMxSorting/Summary`
      - Set **Attribute** = `$IteratorMxSorting/Attribute`
      - Set **SortingDirection** = `$IteratorMxSorting/SortingDirection`
      - Set **MxSorting_MxSheet** = `$NewMxSheet`**
   │ 2. **Retrieve related **MxSorting_MxXPath** via Association from **$NewMxSorting** (Result: **$MxXPath**)**
   │ 3. **Delete**
   │ 4. **Update **$NewMxSorting**
      - Set **MxSorting_MxXPath** = `empty`**
   │ 5. **Add **$$NewMxSorting** to/from list **$MxSortingCommitList****
   │ 6. **Retrieve related **MxSorting_MxXPath** via Association from **$IteratorMxSorting** (Result: **$MxXPathList**)**
   │ 7. **Call Microflow **XLSReport.SUB_Import_XPathList** (Result: **$Variable**)**
   └─ **End Loop**
4. **Commit/Save **$MxSortingCommitList** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.