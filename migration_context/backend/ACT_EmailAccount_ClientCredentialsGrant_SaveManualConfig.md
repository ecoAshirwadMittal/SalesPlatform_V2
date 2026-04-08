# Microflow Detailed Specification: ACT_EmailAccount_ClientCredentialsGrant_SaveManualConfig

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailAccount/isIncomingEmailConfigured`
   ➔ **If [true]:**
      1. **Update **$EmailAccount**
      - Set **MailAddress** = `if($EmailAccount/IsSharedMailbox) then $EmailAccount/MailAddress else $EmailAccount/Username`**
      2. **Retrieve related **EmailAccount_OAuthProvider** via Association from **$EmailAccount** (Result: **$OAuthProvider**)**
      3. **Call Microflow **Email_Connector.SUB_GetNewOAuthToken_ClientCredentialsGrantFlow** (Result: **$NewOAuthToken_CC**)**
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$EmailAccount/isOutgoingEmailConfigured`
         ➔ **If [true]:**
            1. **Update **$EmailAccount**
      - Set **MailAddress** = `if($EmailAccount/IsSharedMailbox) then $EmailAccount/MailAddress else $EmailAccount/Username`**
            2. **Retrieve related **EmailAccount_OAuthProvider** via Association from **$EmailAccount** (Result: **$OAuthProvider**)**
            3. **Call Microflow **Email_Connector.SUB_GetNewOAuthToken_ClientCredentialsGrantFlow** (Result: **$NewOAuthToken_CC**)**
         ➔ **If [false]:**
            1. **Show Message (Error): `Please select either Incoming email protocol or outgoing email protocol.`**
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.