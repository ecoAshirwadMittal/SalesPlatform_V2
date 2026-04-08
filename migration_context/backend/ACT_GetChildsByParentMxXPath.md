# Microflow Detailed Specification: ACT_GetChildsByParentMxXPath

### 📥 Inputs (Parameters)
- **$MxXPath** (Type: XLSReport.MxXPath)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **CreateList**
3. **Create **XLSReport.Exported_ParentXPath** (Result: **$NewExported_ParentXPath**)
      - Set **JSONArray** = `$json`
      - Set **Exported_ParentXPath_MxXPath** = `$MxXPath`**
4. **Add **$$NewExported_ParentXPath** to/from list **$Exported_ParentXPathList****
5. 🏁 **END:** Return `$Exported_ParentXPathList`

**Final Result:** This process concludes by returning a [List] value.