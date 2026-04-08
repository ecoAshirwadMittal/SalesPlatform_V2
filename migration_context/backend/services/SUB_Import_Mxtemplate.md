# Microflow Detailed Specification: SUB_Import_Mxtemplate

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)
- **$CustomExcel** (Type: XLSReport.CustomExcel)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **MxModelReflection.MxObjectType** Filter: `[XLSReport.MxTemplate_InputObject = $MxTemplate]` (Result: **$MxObjectType**)**
2. **Create **XLSReport.MxTemplate** (Result: **$NewMxTemplate**)
      - Set **Name** = `$MxTemplate/Name`
      - Set **Description** = `$MxTemplate/Description`
      - Set **DocumentType** = `$MxTemplate/DocumentType`
      - Set **CSVSeparator** = `$MxTemplate/CSVSeparator`
      - Set **DateTimePresentation** = `$MxTemplate/DateTimePresentation`
      - Set **CustomeDateFormat** = `$MxTemplate/CustomeDateFormat`
      - Set **MxTemplate_InputObject** = `$MxObjectType`
      - Set **QuotationCharacter** = `$MxTemplate/QuotationCharacter`**
3. **Call Microflow **XLSReport.ImportMxCellStyle** (Result: **$Variable**)**
4. **Call Microflow **XLSReport.SUB_Import_MxSheet****
5. **Call Microflow **XLSReport.SUB_Import_CustomExcel****
6. **Commit/Save **$NewMxTemplate** to Database**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.