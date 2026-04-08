# Microflow Detailed Specification: ACT_EmailAccount_LaunchEmailConnectorOverview

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `@Encryption.EncryptionKey != empty and @Encryption.EncryptionKey != ''`
   ➔ **If [true]:**
      1. **DB Retrieve **Email_Connector.EmailTemplate**  (Result: **$FirstEmailTemplate**)**
      2. 🔀 **DECISION:** `$FirstEmailTemplate != empty`
         ➔ **If [true]:**
            1. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
            2. **Maps to Page: **Email_Connector.EmailConnector_Overview****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Create **Email_Connector.EmailTemplate** (Result: **$NewEmailTemplate**)
      - Set **TemplateName** = `'My first template'`
      - Set **ReplyTo** = `'youremail@example.com'`
      - Set **Subject** = `'Excellent, its working!'`
      - Set **Content** = `'<strong>Hello there,</strong> <br />You have successfully sent your first email using the Email templates.<br /><br />You should probably look at the email templates right now.<br />Replace placeholder tokens with values from entity attributes using templates. <br /><br /><strong>Thank you very much, </strong><br /><strong>Team Platform Connectors</strong>'`
      - Set **PlainBody** = `'Hello there, You have successfully sent your first email using the Email templates. You should probably look at the email templates right now. Replace placeholder tokens with values from entity attributes using templates. Thank you very much, Team Platform Connectors'`**
            2. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
            3. **Maps to Page: **Email_Connector.EmailConnector_Overview****
            4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Error): `Please configure a value(32 characters) for the 'EncryptionKey' constant.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.