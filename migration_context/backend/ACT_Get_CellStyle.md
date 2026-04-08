# Microflow Detailed Specification: ACT_Get_CellStyle

### 📥 Inputs (Parameters)
- **$MxData** (Type: XLSReport.MxData)
- **$Name** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **MxData_MxSheet** via Association from **$MxData** (Result: **$MxSheet**)**
2. **Retrieve related **MxSheet_Template** via Association from **$MxSheet** (Result: **$MxTemplate**)**
3. **Retrieve related **MxCellStyle_Template** via Association from **$MxTemplate** (Result: **$MxCellStyleList**)**
4. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/Name = $Name` (Result: **$NewMxCellStyle**)**
5. 🏁 **END:** Return `$NewMxCellStyle`

**Final Result:** This process concludes by returning a [Object] value.