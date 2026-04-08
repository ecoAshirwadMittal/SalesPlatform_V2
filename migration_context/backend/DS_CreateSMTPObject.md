# Microflow Detailed Specification: DS_CreateSMTPObject

### 📥 Inputs (Parameters)
- **$EmailTemplate** (Type: Email_Connector.EmailTemplate)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **EmailTemplateSMTP_EmailTemplate** via Association from **$EmailTemplate** (Result: **$EmailAccount**)**
2. 🔀 **DECISION:** `$EmailAccount =empty`
   ➔ **If [true]:**
      1. **DB Retrieve **Email_Connector.EmailAccount** Filter: `[Username=$EmailTemplate/FromAddress and isOutgoingEmailConfigured]` (Result: **$RetrieveAccountOfTemplate**)**
      2. 🔀 **DECISION:** `$RetrieveAccountOfTemplate != empty`
         ➔ **If [false]:**
            1. **Create **ForgotPassword.EmailTemplateSMTP** (Result: **$NewTemplateSMTP**)
      - Set **EmailTemplateSMTP_EmailTemplate** = `$EmailTemplate`**
            2. 🏁 **END:** Return `$NewTemplateSMTP`
         ➔ **If [true]:**
            1. **Create **ForgotPassword.EmailTemplateSMTP** (Result: **$SetSMTPToTemplate**)
      - Set **EmailTemplateSMTP_EmailAccount** = `$RetrieveAccountOfTemplate`
      - Set **EmailTemplateSMTP_EmailTemplate** = `$EmailTemplate`**
            2. 🏁 **END:** Return `$SetSMTPToTemplate`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$EmailAccount`

**Final Result:** This process concludes by returning a [Object] value.