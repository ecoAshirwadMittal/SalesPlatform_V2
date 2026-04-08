# Microflow Detailed Specification: ACT_ConnectLanguageToTemplate

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailTemplate/ForgotPassword.EmailTemplateLanguage_EmailTemplate/ForgotPassword.EmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language/System.Language != empty`
   ➔ **If [true]:**
      1. **Retrieve related **EmailTemplateLanguage_EmailTemplate** via Association from **$EmailTemplate** (Result: **$CurrentEmailTemplateLanguage**)**
      2. **Retrieve related **EmailTemplateLanguage_Language** via Association from **$CurrentEmailTemplateLanguage** (Result: **$CurrentLanguage**)**
      3. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[ForgotPassword.Configuration_EmailTemplate_Reset/ForgotPassword.Configuration and ForgotPassword.EmailTemplateLanguage_EmailTemplate/ForgotPassword.EmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language = $CurrentLanguage and id != $EmailTemplate]` (Result: **$EmailTemplateListForLanguageComparison**)**
      4. 🔀 **DECISION:** `$EmailTemplateListForLanguageComparison != empty`
         ➔ **If [true]:**
            1. **Show Message (Error): `{1} is already associated with the Template ! Please use another Language !`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Retrieve related **Configuration_EmailTemplate_Reset** via Association from **$EmailTemplate** (Result: **$CurrentEmailConfiguration**)**
            2. 🔀 **DECISION:** `$CurrentEmailConfiguration = empty`
               ➔ **If [false]:**
                  1. **Retrieve related **EmailTemplateSMTP_EmailTemplate** via Association from **$EmailTemplate** (Result: **$CurrentEmailTemplateSMTP**)**
                  2. **Update **$CurrentEmailTemplateSMTP** (and Save to DB)
      - Set **EmailTemplateSMTP_EmailAccount** = `$CurrentEmailTemplateSMTP/ForgotPassword.EmailTemplateSMTP_EmailAccount/Email_Connector.EmailAccount`
      - Set **EmailTemplateSMTP_EmailTemplate** = `$EmailTemplate`**
                  3. **Update **$CurrentEmailTemplateLanguage** (and Save to DB)
      - Set **EmailTemplateLanguage_Language** = `$CurrentEmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language/System.Language`
      - Set **EmailTemplateLanguage_EmailTemplate** = `$EmailTemplate`**
                  4. **Commit/Save **$EmailTemplate** to Database**
                  5. **Close current page/popup**
                  6. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Create **ForgotPassword.Configuration** (Result: **$NewConfiguration**)
      - Set **Configuration_EmailTemplate_Reset** = `$EmailTemplate`**
                  2. **Retrieve related **EmailTemplateSMTP_EmailTemplate** via Association from **$EmailTemplate** (Result: **$CurrentEmailTemplateSMTP**)**
                  3. **Update **$CurrentEmailTemplateSMTP** (and Save to DB)
      - Set **EmailTemplateSMTP_EmailAccount** = `$CurrentEmailTemplateSMTP/ForgotPassword.EmailTemplateSMTP_EmailAccount/Email_Connector.EmailAccount`
      - Set **EmailTemplateSMTP_EmailTemplate** = `$EmailTemplate`**
                  4. **Update **$CurrentEmailTemplateLanguage** (and Save to DB)
      - Set **EmailTemplateLanguage_Language** = `$CurrentEmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language/System.Language`
      - Set **EmailTemplateLanguage_EmailTemplate** = `$EmailTemplate`**
                  5. **Commit/Save **$EmailTemplate** to Database**
                  6. **Close current page/popup**
                  7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[ForgotPassword.Configuration_EmailTemplate_Reset/ForgotPassword.Configuration and id != $EmailTemplate ]` (Result: **$EmailTemplateList**)**
      2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/ForgotPassword.EmailTemplateLanguage_EmailTemplate/ForgotPassword.EmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language = empty` (Result: **$EmailTemplateWithoutLanguage**)**
      3. **AggregateList**
      4. 🔀 **DECISION:** `$EmailTemplateCount > 0`
         ➔ **If [true]:**
            1. **Show Message (Error): `There is a template available without any language associated ! Please use any language!`**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Retrieve related **EmailTemplateLanguage_EmailTemplate** via Association from **$EmailTemplate** (Result: **$CurrentEmailTemplateLanguage**)**
            2. **Retrieve related **EmailTemplateLanguage_Language** via Association from **$CurrentEmailTemplateLanguage** (Result: **$CurrentLanguage**)**
            3. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[ForgotPassword.Configuration_EmailTemplate_Reset/ForgotPassword.Configuration and ForgotPassword.EmailTemplateLanguage_EmailTemplate/ForgotPassword.EmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language = $CurrentLanguage and id != $EmailTemplate]` (Result: **$EmailTemplateListForLanguageComparison**)**
            4. 🔀 **DECISION:** `$EmailTemplateListForLanguageComparison != empty`
               ➔ **If [true]:**
                  1. **Show Message (Error): `{1} is already associated with the Template ! Please use another Language !`**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Retrieve related **Configuration_EmailTemplate_Reset** via Association from **$EmailTemplate** (Result: **$CurrentEmailConfiguration**)**
                  2. 🔀 **DECISION:** `$CurrentEmailConfiguration = empty`
                     ➔ **If [false]:**
                        1. **Retrieve related **EmailTemplateSMTP_EmailTemplate** via Association from **$EmailTemplate** (Result: **$CurrentEmailTemplateSMTP**)**
                        2. **Update **$CurrentEmailTemplateSMTP** (and Save to DB)
      - Set **EmailTemplateSMTP_EmailAccount** = `$CurrentEmailTemplateSMTP/ForgotPassword.EmailTemplateSMTP_EmailAccount/Email_Connector.EmailAccount`
      - Set **EmailTemplateSMTP_EmailTemplate** = `$EmailTemplate`**
                        3. **Update **$CurrentEmailTemplateLanguage** (and Save to DB)
      - Set **EmailTemplateLanguage_Language** = `$CurrentEmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language/System.Language`
      - Set **EmailTemplateLanguage_EmailTemplate** = `$EmailTemplate`**
                        4. **Commit/Save **$EmailTemplate** to Database**
                        5. **Close current page/popup**
                        6. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Create **ForgotPassword.Configuration** (Result: **$NewConfiguration**)
      - Set **Configuration_EmailTemplate_Reset** = `$EmailTemplate`**
                        2. **Retrieve related **EmailTemplateSMTP_EmailTemplate** via Association from **$EmailTemplate** (Result: **$CurrentEmailTemplateSMTP**)**
                        3. **Update **$CurrentEmailTemplateSMTP** (and Save to DB)
      - Set **EmailTemplateSMTP_EmailAccount** = `$CurrentEmailTemplateSMTP/ForgotPassword.EmailTemplateSMTP_EmailAccount/Email_Connector.EmailAccount`
      - Set **EmailTemplateSMTP_EmailTemplate** = `$EmailTemplate`**
                        4. **Update **$CurrentEmailTemplateLanguage** (and Save to DB)
      - Set **EmailTemplateLanguage_Language** = `$CurrentEmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language/System.Language`
      - Set **EmailTemplateLanguage_EmailTemplate** = `$EmailTemplate`**
                        5. **Commit/Save **$EmailTemplate** to Database**
                        6. **Close current page/popup**
                        7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.