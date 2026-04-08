# Microflow Detailed Specification: ACT_GetOrCreate_MxSheet

### 📥 Inputs (Parameters)
- **$MappingParent** (Type: XLSReport.MxCellStyle)
- **$Name** (Type: Variable)
- **$Sequence** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxCellStyle_Template** via Association from **$MappingParent** (Result: **$MxTemplate**)**
2. **Retrieve related **MxSheet_Template** via Association from **$MxTemplate** (Result: **$MxSheetList**)**
3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Name = $Name and $currentObject/Sequence = $Sequence` (Result: **$NewMxSheet**)**

**Final Result:** This process concludes by returning a [Object] value.