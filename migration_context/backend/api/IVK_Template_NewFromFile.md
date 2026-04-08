# Microflow Detailed Specification: IVK_Template_NewFromFile

### ⚙️ Execution Flow (Logic Steps)
1. **Create **ExcelImporter.TemplateDocument** (Result: **$NewTemplateDocument**)**
2. **Create **ExcelImporter.Template** (Result: **$NewTemplate**)**
3. **Update **$NewTemplateDocument**
      - Set **TemplateDocument_Template** = `$NewTemplate`**
4. **Maps to Page: **ExcelImporter.Template_New_FromDocument****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.