# Microflow Detailed Specification: IVK_SortingSave

### 📥 Inputs (Parameters)
- **$MxSorting** (Type: XLSReport.MxSorting)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxSorting_MxXPath** via Association from **$MxSorting** (Result: **$MxXPath**)**
2. 🔀 **DECISION:** `$MxXPath != empty`
   ➔ **If [false]:**
      1. **Call Microflow **XLSReport.ACr_Sorting****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **MxSorting_MxSheet** via Association from **$MxSorting** (Result: **$MxSheet**)**
      2. **DB Retrieve **XLSReport.MxReferenceHandling** Filter: `[XLSReport.MxReferenceHandling_MxSheet = $MxSheet]` (Result: **$MxReferenceHandlingList**)**
      3. **Call Microflow **XLSReport.XPath_Validate** (Result: **$XpathCommit**)**
      4. 🔀 **DECISION:** `$XpathCommit`
         ➔ **If [true]:**
            1. **Commit/Save **$MxReferenceHandlingList** to Database**
            2. **Call Microflow **XLSReport.XPath_Delete_Subs****
            3. **Call Microflow **XLSReport.SF_FindAttribute** (Result: **$Attribute**)**
            4. 🔀 **DECISION:** `$MxSorting/Sequence > 0`
               ➔ **If [true]:**
                  1. **Update **$MxSorting** (and Save to DB)
      - Set **Summary** = `$Attribute/CompleteName`
      - Set **SortingDirection** = `if $MxSorting/SortingDirection = empty then XLSReport.SortingDirection.Asc else $MxSorting/SortingDirection`
      - Set **Attribute** = `$Attribute/AttributeName`**
                  2. **Close current page/popup**
                  3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **DB Retrieve **XLSReport.MxSorting** Filter: `[XLSReport.MxSorting_MxSheet = $MxSorting/XLSReport.MxSorting_MxSheet] [id != $MxSorting]` (Result: **$MxSortingList**)**
                  2. **AggregateList**
                  3. **Update **$MxSorting**
      - Set **Sequence** = `if $count != empty then $count + 1 else 1`**
                  4. **Update **$MxSorting** (and Save to DB)
      - Set **Summary** = `$Attribute/CompleteName`
      - Set **SortingDirection** = `if $MxSorting/SortingDirection = empty then XLSReport.SortingDirection.Asc else $MxSorting/SortingDirection`
      - Set **Attribute** = `$Attribute/AttributeName`**
                  5. **Close current page/popup**
                  6. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.