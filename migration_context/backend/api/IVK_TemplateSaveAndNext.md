# Microflow Detailed Specification: IVK_TemplateSaveAndNext

### 📥 Inputs (Parameters)
- **$Template** (Type: XLSReport.MxTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Voldoet** = `true`**
2. 🔀 **DECISION:** `$Template/Name = empty`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$Template/DocumentType = empty`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$Voldoet`
               ➔ **If [true]:**
                  1. **Update **$Template** (and Save to DB)**
                  2. 🔀 **DECISION:** `$Template/DocumentType = XLSReport.DocumentType.XLS or $Template/DocumentType = XLSReport.DocumentType.XLSX`
                     ➔ **If [true]:**
                        1. **Create **XLSReport.MxCellStyle** (Result: **$NewMxCellStyle**)
      - Set **MxCellStyle_Template** = `$Template`
      - Set **Name** = `'Default'`
      - Set **TextAlignment** = `XLSReport.TextAlignment.Left`
      - Set **TextVerticalalignment** = `XLSReport.TextVerticalAlignment.Top`**
                        2. **Close current page/popup**
                        3. **Call Microflow **XLSReport.IVK_TemplateEdit****
                        4. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Close current page/popup**
                        2. **Call Microflow **XLSReport.IVK_TemplateEdit****
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update Variable **$Voldoet** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$Voldoet`
               ➔ **If [true]:**
                  1. **Update **$Template** (and Save to DB)**
                  2. 🔀 **DECISION:** `$Template/DocumentType = XLSReport.DocumentType.XLS or $Template/DocumentType = XLSReport.DocumentType.XLSX`
                     ➔ **If [true]:**
                        1. **Create **XLSReport.MxCellStyle** (Result: **$NewMxCellStyle**)
      - Set **MxCellStyle_Template** = `$Template`
      - Set **Name** = `'Default'`
      - Set **TextAlignment** = `XLSReport.TextAlignment.Left`
      - Set **TextVerticalalignment** = `XLSReport.TextVerticalAlignment.Top`**
                        2. **Close current page/popup**
                        3. **Call Microflow **XLSReport.IVK_TemplateEdit****
                        4. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Close current page/popup**
                        2. **Call Microflow **XLSReport.IVK_TemplateEdit****
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Update Variable **$Voldoet** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `$Template/DocumentType = empty`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$Voldoet`
               ➔ **If [true]:**
                  1. **Update **$Template** (and Save to DB)**
                  2. 🔀 **DECISION:** `$Template/DocumentType = XLSReport.DocumentType.XLS or $Template/DocumentType = XLSReport.DocumentType.XLSX`
                     ➔ **If [true]:**
                        1. **Create **XLSReport.MxCellStyle** (Result: **$NewMxCellStyle**)
      - Set **MxCellStyle_Template** = `$Template`
      - Set **Name** = `'Default'`
      - Set **TextAlignment** = `XLSReport.TextAlignment.Left`
      - Set **TextVerticalalignment** = `XLSReport.TextVerticalAlignment.Top`**
                        2. **Close current page/popup**
                        3. **Call Microflow **XLSReport.IVK_TemplateEdit****
                        4. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Close current page/popup**
                        2. **Call Microflow **XLSReport.IVK_TemplateEdit****
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update Variable **$Voldoet** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$Voldoet`
               ➔ **If [true]:**
                  1. **Update **$Template** (and Save to DB)**
                  2. 🔀 **DECISION:** `$Template/DocumentType = XLSReport.DocumentType.XLS or $Template/DocumentType = XLSReport.DocumentType.XLSX`
                     ➔ **If [true]:**
                        1. **Create **XLSReport.MxCellStyle** (Result: **$NewMxCellStyle**)
      - Set **MxCellStyle_Template** = `$Template`
      - Set **Name** = `'Default'`
      - Set **TextAlignment** = `XLSReport.TextAlignment.Left`
      - Set **TextVerticalalignment** = `XLSReport.TextVerticalAlignment.Top`**
                        2. **Close current page/popup**
                        3. **Call Microflow **XLSReport.IVK_TemplateEdit****
                        4. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Close current page/popup**
                        2. **Call Microflow **XLSReport.IVK_TemplateEdit****
                        3. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.