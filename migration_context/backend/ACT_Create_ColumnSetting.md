# Microflow Detailed Specification: ACT_Create_ColumnSetting

### 📥 Inputs (Parameters)
- **$MappingParent** (Type: XLSReport.MxSheet)
- **$ColumnIndex** (Type: Variable)
- **$AutoSize** (Type: Variable)
- **$ColumnWidth** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **XLSReport.MxColumnSettings** (Result: **$NewMxColumnSettings**)
      - Set **ColumnIndex** = `$ColumnIndex`
      - Set **AutoSize** = `$AutoSize`
      - Set **ColumnWidth** = `$ColumnWidth`
      - Set **ColumnSettings_MxSheet** = `$MappingParent`**
2. 🏁 **END:** Return `$NewMxColumnSettings`

**Final Result:** This process concludes by returning a [Object] value.