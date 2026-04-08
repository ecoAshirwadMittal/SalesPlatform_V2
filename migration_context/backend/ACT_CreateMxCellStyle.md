# Microflow Detailed Specification: ACT_CreateMxCellStyle

### 📥 Inputs (Parameters)
- **$MappingParent** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **XLSReport.MxCellStyle** (Result: **$NewMxCellStyle**)
      - Set **MxCellStyle_Template** = `$MappingParent`**
2. 🏁 **END:** Return `$NewMxCellStyle`

**Final Result:** This process concludes by returning a [Object] value.