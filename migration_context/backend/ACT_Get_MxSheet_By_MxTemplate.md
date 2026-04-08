# Microflow Detailed Specification: ACT_Get_MxSheet_By_MxTemplate

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)
- **$Name** (Type: Variable)
- **$Sequence** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxSheet_Template** via Association from **$MxTemplate** (Result: **$MxSheetList**)**
2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Name = $Name and $currentObject/Sequence = $Sequence` (Result: **$NewMxSheet**)**

**Final Result:** This process concludes by returning a [Object] value.