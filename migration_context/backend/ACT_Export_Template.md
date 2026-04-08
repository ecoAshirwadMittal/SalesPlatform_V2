# Microflow Detailed Specification: ACT_Export_Template

### 📥 Inputs (Parameters)
- **$MxTemplate** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **XLSReport.CustomExcel** (Result: **$NewMxTemplateExport**)
      - Set **Name** = `$MxTemplate/Name + '.json'`
      - Set **DeleteAfterDownload** = `true`**
2. **ExportXml**
3. **DownloadFile**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.