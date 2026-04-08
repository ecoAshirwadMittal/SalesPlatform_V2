# Microflow Detailed Specification: ACT_SelectedConfiguration_FetchEmailServerConfigAndShowConfigPage

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailAccount/isEmailConfigAutoDetect`
   ➔ **If [true]:**
      1. **JavaCallAction**
      2. 🔀 **DECISION:** `$EmailAccount/isOAuthUsed and $EmailAccount/Email_Connector.EmailAccount_OAuthProvider/Email_Connector.OAuthProvider/Name = '--- New configuration ---'`
         ➔ **If [false]:**
            1. **Update **$EmailProvider**
      - Set **Username** = `$EmailAccount/Username`
      - Set **MailAddress** = `$EmailAccount/MailAddress`
      - Set **Password** = `$EmailAccount/Password`
      - Set **FromDisplayName** = `$EmailAccount/FromDisplayName`
      - Set **IsSharedMailbox** = `$EmailAccount/IsSharedMailbox`
      - Set **isOAuthUsed** = `$EmailAccount/isOAuthUsed`
      - Set **AuthType** = `$EmailAccount/Email_Connector.EmailAccount_OAuthProvider/Email_Connector.OAuthProvider/OAuthType`
      - Set **EmailProvider_OAuthProvider** = `$EmailAccount/Email_Connector.EmailAccount_OAuthProvider/Email_Connector.OAuthProvider`**
            2. **Create **Email_Connector.SelectedConfiguration** (Result: **$NewSelectedConfiguration**)
      - Set **SelectedConfiguration_EmailProvider** = `$EmailProvider`**
            3. **Close current page/popup**
            4. **Maps to Page: **Email_Connector.EmailAccount_ConfigProtocol_Auto_NewEdit****
            5. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Show Message (Information): `Please select OAuth configuration.`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Close current page/popup**
      2. **Maps to Page: **Email_Connector.EmailAccount_ConfigProtocol_Manual_NewEdit****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.