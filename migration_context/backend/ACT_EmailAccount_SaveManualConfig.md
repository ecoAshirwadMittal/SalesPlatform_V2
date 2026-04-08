# Microflow Detailed Specification: ACT_EmailAccount_SaveManualConfig

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailAccount/isIncomingEmailConfigured`
   ➔ **If [true]:**
      1. **Update **$EmailAccount**
      - Set **MailAddress** = `if($EmailAccount/IsSharedMailbox) then $EmailAccount/MailAddress else $EmailAccount/Username`**
      2. 🔀 **DECISION:** `$EmailAccount/isOAuthUsed`
         ➔ **If [true]:**
            1. **Call Microflow **Email_Connector.SUB_EmailAccount_Save****
            2. **Close current page/popup**
            3. 🔀 **DECISION:** `$EmailAccount/isOAuthUsed`
               ➔ **If [false]:**
                  1. **Maps to Page: **Email_Connector.EmailAccount_Added_Confirmation****
                  2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Maps to Page: **Email_Connector.RedirectToProviderMessage_View****
                  2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Email_Connector.SUB_EmailAccount_CheckServerConnection** (Result: **$isConnected**)**
            2. 🔀 **DECISION:** `$isConnected`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Call Microflow **Email_Connector.SUB_EmailAccount_Save****
                  2. **Close current page/popup**
                  3. 🔀 **DECISION:** `$EmailAccount/isOAuthUsed`
                     ➔ **If [false]:**
                        1. **Maps to Page: **Email_Connector.EmailAccount_Added_Confirmation****
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Maps to Page: **Email_Connector.RedirectToProviderMessage_View****
                        2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$EmailAccount/isOutgoingEmailConfigured`
         ➔ **If [true]:**
            1. **Update **$EmailAccount**
      - Set **MailAddress** = `if($EmailAccount/IsSharedMailbox) then $EmailAccount/MailAddress else $EmailAccount/Username`**
            2. 🔀 **DECISION:** `$EmailAccount/isOAuthUsed`
               ➔ **If [true]:**
                  1. **Call Microflow **Email_Connector.SUB_EmailAccount_Save****
                  2. **Close current page/popup**
                  3. 🔀 **DECISION:** `$EmailAccount/isOAuthUsed`
                     ➔ **If [false]:**
                        1. **Maps to Page: **Email_Connector.EmailAccount_Added_Confirmation****
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Maps to Page: **Email_Connector.RedirectToProviderMessage_View****
                        2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **Email_Connector.SUB_EmailAccount_CheckServerConnection** (Result: **$isConnected**)**
                  2. 🔀 **DECISION:** `$isConnected`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Call Microflow **Email_Connector.SUB_EmailAccount_Save****
                        2. **Close current page/popup**
                        3. 🔀 **DECISION:** `$EmailAccount/isOAuthUsed`
                           ➔ **If [false]:**
                              1. **Maps to Page: **Email_Connector.EmailAccount_Added_Confirmation****
                              2. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Maps to Page: **Email_Connector.RedirectToProviderMessage_View****
                              2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Show Message (Error): `Please select either Incoming email protocol or outgoing email protocol.`**
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.