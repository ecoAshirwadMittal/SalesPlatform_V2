# Microflow Detailed Specification: DS_CreateLanguageObject

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **EmailTemplateLanguage_EmailTemplate** via Association from **$EmailTemplate** (Result: **$TemplateLanguage**)**
2. 🔀 **DECISION:** `$TemplateLanguage=empty`
   ➔ **If [true]:**
      1. **Create **ForgotPassword.EmailTemplateLanguage** (Result: **$NewTemplateLanguage**)
      - Set **EmailTemplateLanguage_EmailTemplate** = `$EmailTemplate`**
      2. 🏁 **END:** Return `$NewTemplateLanguage`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$TemplateLanguage`

**Final Result:** This process concludes by returning a [Object] value.