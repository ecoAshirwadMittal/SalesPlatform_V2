# Microflow Detailed Specification: IVK_TemplateDoc_Cancel

### 📥 Inputs (Parameters)
- **$TemplateDocument** (Type: ExcelImporter.TemplateDocument)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **TemplateDocument_Template** via Association from **$TemplateDocument** (Result: **$Template**)**
2. **Rollback**
3. **Rollback**
4. **Close current page/popup**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.