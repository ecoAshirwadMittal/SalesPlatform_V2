# Microflow Detailed Specification: SUB_Import_MxRowSettings

### 📥 Inputs (Parameters)
- **$MxSheet** (Type: XLSReport.MxSheet)
- **$NewMxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxRowSettings_MxSheet** via Association from **$MxSheet** (Result: **$MxRowSettingsList**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorMxRowSettings** in **$MxRowSettingsList**
   │ 1. **Create **XLSReport.MxRowSettings** (Result: **$NewMxRowSettings**)
      - Set **RowIndex** = `$IteratorMxRowSettings/RowIndex`
      - Set **DefaultHeight** = `$IteratorMxRowSettings/DefaultHeight`
      - Set **RowHeight** = `$IteratorMxRowSettings/RowHeight`
      - Set **MxRowSettings_MxSheet** = `$NewMxSheet`**
   │ 2. **Add **$$MxRowSettingsCommitList** to/from list **$MxRowSettingsCommitList****
   └─ **End Loop**
4. **Commit/Save **$MxRowSettingsCommitList** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.