# Microflow Detailed Specification: ACT_EmailAccount_ClientCredentialsGrant_SaveAutoConfig

### 📥 Inputs (Parameters)
- **$EmailProvider** (Type: Email_Connector.EmailProvider)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SelectedConfiguration_EmailProvider** via Association from **$EmailProvider** (Result: **$SelectedConfiguration**)**
2. **Create **Email_Connector.IncomingEmailConfiguration** (Result: **$NewIncomingEmailConfiguration**)
      - Set **IncomingProtocol** = `$SelectedConfiguration/IncomingProtocol`
      - Set **ServerHost** = `$SelectedConfiguration/IncomingHost`
      - Set **ServerPort** = `$SelectedConfiguration/IncomingPort`
      - Set **BatchSize** = `10`
      - Set **FetchStrategy** = `Email_Connector.ENUM_FetchStrategy.Latest`**
3. **Create **Email_Connector.OutgoingEmailConfiguration** (Result: **$NewOutgoingEmailConfiguration**)
      - Set **OutgoingProtocol** = `$SelectedConfiguration/OutgoingProtocol`
      - Set **SSL** = `$SelectedConfiguration/OutgoingSSL`
      - Set **TLS** = `$SelectedConfiguration/OutgoingTLS`
      - Set **SendMaxAttempts** = `3`
      - Set **ServerHost** = `$SelectedConfiguration/OutgoingHost`
      - Set **ServerPort** = `$SelectedConfiguration/OutgoingPort`**
4. 🔀 **DECISION:** `$SelectedConfiguration/IncomingProtocol!=empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$EmailProvider/ReceiveEmails and $SelectedConfiguration/IncomingHost=empty`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$EmailProvider/SendEmails and $SelectedConfiguration/OutgoingHost=empty`
               ➔ **If [true]:**
                  1. **Show Message (Error): `Please select outgoing email protocol or uncheck Send Emails checkbox.`**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Create **Email_Connector.EmailAccount** (Result: **$NewEmailAccount**)
      - Set **OutgoingEmailConfiguration_EmailAccount** = `if($NewOutgoingEmailConfiguration/OutgoingProtocol !=empty) then $NewOutgoingEmailConfiguration else empty`
      - Set **IncomingEmailConfiguration_EmailAccount** = `if($NewIncomingEmailConfiguration/IncomingProtocol!=empty) then $NewIncomingEmailConfiguration else empty`
      - Set **Username** = `$EmailProvider/Username`
      - Set **MailAddress** = `if($EmailProvider/IsSharedMailbox) then $EmailProvider/MailAddress else $EmailProvider/Username`
      - Set **Password** = `$EmailProvider/Password`
      - Set **isIncomingEmailConfigured** = `$SelectedConfiguration/Email_Connector.SelectedConfiguration_EmailProvider/Email_Connector.EmailProvider/ReceiveEmails`
      - Set **isOutgoingEmailConfigured** = `$SelectedConfiguration/Email_Connector.SelectedConfiguration_EmailProvider/Email_Connector.EmailProvider/SendEmails`
      - Set **FromDisplayName** = `$EmailProvider/FromDisplayName`
      - Set **IsSharedMailbox** = `$EmailProvider/IsSharedMailbox`
      - Set **isOAuthUsed** = `$EmailProvider/isOAuthUsed`
      - Set **EmailAccount_OAuthProvider** = `$EmailProvider/Email_Connector.EmailProvider_OAuthProvider/Email_Connector.OAuthProvider`**
                  2. **Retrieve related **EmailAccount_OAuthProvider** via Association from **$NewEmailAccount** (Result: **$OAuthProvider**)**
                  3. **Call Microflow **Email_Connector.SUB_GetNewOAuthToken_ClientCredentialsGrantFlow** (Result: **$NewOAuthToken_CC**)**
         ➔ **If [true]:**
            1. **Show Message (Error): `Please select incoming email protocol or uncheck Receive Emails checkbox.`**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$SelectedConfiguration/OutgoingProtocol!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$EmailProvider/ReceiveEmails and $SelectedConfiguration/IncomingHost=empty`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$EmailProvider/SendEmails and $SelectedConfiguration/OutgoingHost=empty`
                     ➔ **If [true]:**
                        1. **Show Message (Error): `Please select outgoing email protocol or uncheck Send Emails checkbox.`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Create **Email_Connector.EmailAccount** (Result: **$NewEmailAccount**)
      - Set **OutgoingEmailConfiguration_EmailAccount** = `if($NewOutgoingEmailConfiguration/OutgoingProtocol !=empty) then $NewOutgoingEmailConfiguration else empty`
      - Set **IncomingEmailConfiguration_EmailAccount** = `if($NewIncomingEmailConfiguration/IncomingProtocol!=empty) then $NewIncomingEmailConfiguration else empty`
      - Set **Username** = `$EmailProvider/Username`
      - Set **MailAddress** = `if($EmailProvider/IsSharedMailbox) then $EmailProvider/MailAddress else $EmailProvider/Username`
      - Set **Password** = `$EmailProvider/Password`
      - Set **isIncomingEmailConfigured** = `$SelectedConfiguration/Email_Connector.SelectedConfiguration_EmailProvider/Email_Connector.EmailProvider/ReceiveEmails`
      - Set **isOutgoingEmailConfigured** = `$SelectedConfiguration/Email_Connector.SelectedConfiguration_EmailProvider/Email_Connector.EmailProvider/SendEmails`
      - Set **FromDisplayName** = `$EmailProvider/FromDisplayName`
      - Set **IsSharedMailbox** = `$EmailProvider/IsSharedMailbox`
      - Set **isOAuthUsed** = `$EmailProvider/isOAuthUsed`
      - Set **EmailAccount_OAuthProvider** = `$EmailProvider/Email_Connector.EmailProvider_OAuthProvider/Email_Connector.OAuthProvider`**
                        2. **Retrieve related **EmailAccount_OAuthProvider** via Association from **$NewEmailAccount** (Result: **$OAuthProvider**)**
                        3. **Call Microflow **Email_Connector.SUB_GetNewOAuthToken_ClientCredentialsGrantFlow** (Result: **$NewOAuthToken_CC**)**
               ➔ **If [true]:**
                  1. **Show Message (Error): `Please select incoming email protocol or uncheck Receive Emails checkbox.`**
                  2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Show Message (Error): `Please select either Incoming email protocol or outgoing email protocol.`**
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.