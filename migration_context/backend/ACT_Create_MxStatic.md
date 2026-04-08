# Microflow Detailed Specification: ACT_Create_MxStatic

### 📥 Inputs (Parameters)
- **$MappingParent** (Type: XLSReport.MxSheet)
- **$Name** (Type: Variable)
- **$ColumnPlace** (Type: Variable)
- **$RowPlace** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **XLSReport.MxStatic** (Result: **$NewMxStatic**)
      - Set **ColumnPlace** = `$ColumnPlace`
      - Set **RowPlace** = `$RowPlace`
      - Set **Name** = `$Name`
      - Set **MxData_MxSheet** = `$MappingParent`**
2. 🏁 **END:** Return `$NewMxStatic`

**Final Result:** This process concludes by returning a [Object] value.