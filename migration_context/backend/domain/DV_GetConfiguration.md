# Microflow Detailed Specification: DV_GetConfiguration

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **ForgotPassword.Configuration**  (Result: **$Configuration**)**
2. 🔀 **DECISION:** `$Configuration != empty`
   ➔ **If [true]:**
      1. **DB Retrieve **Email_Connector.EmailTemplate**  (Result: **$EmailTemplateList**)**
      2. 🔀 **DECISION:** `$EmailTemplateList!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$Configuration/ForgotPassword.Configuration_EmailTemplate_Reset=empty`
               ➔ **If [true]:**
                  1. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[contains(TemplateName,'_Reset')]` (Result: **$EmailTemplateReset**)**
                  2. 🔀 **DECISION:** `$EmailTemplateReset=empty`
                     ➔ **If [false]:**
                        1. **Update **$Configuration**
      - Set **Configuration_EmailTemplate_Reset** = `$EmailTemplateReset`**
                        2. 🔀 **DECISION:** `$Configuration/ForgotPassword.Configuration_EmailTemplate_Signup=empty`
                           ➔ **If [true]:**
                              1. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[contains(TemplateName,'_Signup')]` (Result: **$EmailTemplateSignup**)**
                              2. 🔀 **DECISION:** `$EmailTemplateSignup=empty`
                                 ➔ **If [false]:**
                                    1. **Update **$Configuration** (and Save to DB)
      - Set **Configuration_EmailTemplate_Signup** = `$EmailTemplateSignup`**
                                    2. 🏁 **END:** Return `$Configuration`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$Configuration`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$Configuration`
                     ➔ **If [true]:**
                        1. 🔀 **DECISION:** `$Configuration/ForgotPassword.Configuration_EmailTemplate_Signup=empty`
                           ➔ **If [true]:**
                              1. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[contains(TemplateName,'_Signup')]` (Result: **$EmailTemplateSignup**)**
                              2. 🔀 **DECISION:** `$EmailTemplateSignup=empty`
                                 ➔ **If [false]:**
                                    1. **Update **$Configuration** (and Save to DB)
      - Set **Configuration_EmailTemplate_Signup** = `$EmailTemplateSignup`**
                                    2. 🏁 **END:** Return `$Configuration`
                                 ➔ **If [true]:**
                                    1. 🏁 **END:** Return `$Configuration`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return `$Configuration`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$Configuration/ForgotPassword.Configuration_EmailTemplate_Signup=empty`
                     ➔ **If [true]:**
                        1. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[contains(TemplateName,'_Signup')]` (Result: **$EmailTemplateSignup**)**
                        2. 🔀 **DECISION:** `$EmailTemplateSignup=empty`
                           ➔ **If [false]:**
                              1. **Update **$Configuration** (and Save to DB)
      - Set **Configuration_EmailTemplate_Signup** = `$EmailTemplateSignup`**
                              2. 🏁 **END:** Return `$Configuration`
                           ➔ **If [true]:**
                              1. 🏁 **END:** Return `$Configuration`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `$Configuration`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `$Configuration`
   ➔ **If [false]:**
      1. **Create **ForgotPassword.Configuration** (Result: **$NewConfiguration**)**
      2. 🏁 **END:** Return `$NewConfiguration`

**Final Result:** This process concludes by returning a [Object] value.