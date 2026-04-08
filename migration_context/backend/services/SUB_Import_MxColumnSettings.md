# Microflow Detailed Specification: SUB_Import_MxColumnSettings

### 📥 Inputs (Parameters)
- **$MxSheet** (Type: XLSReport.MxSheet)
- **$NewMxSheet** (Type: XLSReport.MxSheet)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ColumnSettings_MxSheet** via Association from **$MxSheet** (Result: **$MxColumnSettingsList**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorMxColumnSettings** in **$MxColumnSettingsList**
   │ 1. **Create **XLSReport.MxColumnSettings** (Result: **$NewMxColumnSettings**)
      - Set **ColumnIndex** = `$IteratorMxColumnSettings/ColumnIndex`
      - Set **AutoSize** = `$IteratorMxColumnSettings/AutoSize`
      - Set **ColumnWidth** = `$IteratorMxColumnSettings/ColumnWidth`
      - Set **ColumnSettings_MxSheet** = `$NewMxSheet`**
   │ 2. **Add **$$NewMxColumnSettings** to/from list **$MxColumnSettingsCommitList****
   └─ **End Loop**
4. **Commit/Save **$MxColumnSettingsCommitList** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.