# Microflow Detailed Specification: SUB_ImportMxStatic

### 📥 Inputs (Parameters)
- **$NewMxSheet** (Type: XLSReport.MxSheet)
- **$MxStatic** (Type: XLSReport.MxStatic)
- **$NewMxTemplate** (Type: XLSReport.MxTemplate)
- **$NewMxColumn** (Type: XLSReport.MxColumn)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **XLSReport.StringToMxCellStyle** (Result: **$MxCellStyle**)**
2. **Retrieve related **MxStatic_MxObjectMember** via Association from **$MxStatic** (Result: **$MxObjectMember**)**
3. **Create **XLSReport.MxStatic** (Result: **$NewMxStatic**)
      - Set **ColumnPlace** = `$MxStatic/ColumnPlace`
      - Set **RowPlace** = `$MxStatic/RowPlace`
      - Set **StaticType** = `$MxStatic/StaticType`
      - Set **AggregateFunction** = `$MxStatic/AggregateFunction`
      - Set **Name** = `$MxStatic/Name`
      - Set **Status** = `$MxStatic/Status`
      - Set **MxStatic_MxColumn** = `$NewMxColumn`
      - Set **MxStatic_MxObjectMember** = `$MxObjectMember`
      - Set **MxData_MxSheet** = `$NewMxSheet`
      - Set **MxData_MxCellStyle** = `$MxCellStyle`**
4. **DB Retrieve **XLSReport.MxXPath** Filter: `[XLSReport.MxXPath_MxData = $NewMxStatic]` (Result: **$MxXPath**)**
5. **Call Microflow **XLSReport.SUB_Import_XPathList** (Result: **$Variable**)**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.