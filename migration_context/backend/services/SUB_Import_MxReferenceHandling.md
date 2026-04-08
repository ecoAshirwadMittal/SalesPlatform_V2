# Microflow Detailed Specification: SUB_Import_MxReferenceHandling

### 📥 Inputs (Parameters)
- **$MxSheet** (Type: XLSReport.MxSheet)
- **$NewMxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxReferenceHandling_MxSheet** via Association from **$MxSheet** (Result: **$MxReferenceHandlingList**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorMxReferenceHandling** in **$MxReferenceHandlingList**
   │ 1. **Create **XLSReport.MxReferenceHandling** (Result: **$NewMxReferenceHandling**)
      - Set **Reference** = `$IteratorMxReferenceHandling/Reference`
      - Set **JoinType** = `$IteratorMxReferenceHandling/JoinType`
      - Set **MxReferenceHandling_MxSheet** = `$NewMxSheet`**
   │ 2. **Add **$$NewMxReferenceHandling** to/from list **$MxReferenceHandlingCommitList****
   └─ **End Loop**
4. **Commit/Save **$MxReferenceHandlingCommitList** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.