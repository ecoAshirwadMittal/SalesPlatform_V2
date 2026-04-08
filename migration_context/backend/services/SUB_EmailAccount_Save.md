# Microflow Detailed Specification: SUB_EmailAccount_Save

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailAccount/isIncomingEmailConfigured`
   ➔ **If [false]:**
      1. **Retrieve related **IncomingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$IncomingEmailConfiguration_2**)**
   ➔ **If [true]:**
      1. **Call Microflow **Email_Connector.VAL_BatchDetails** (Result: **$BatchDetailsValidated**)**
      2. 🔀 **DECISION:** `$BatchDetailsValidated`
         ➔ **If [true]:**
            1. **Update **$EmailAccount**
      - Set **isIncomingEmailConfigured** = `true`**
            2. **Call Microflow **Email_Connector.SUB_EmailAccount_SubscribeForEmailNotification****
            3. **Retrieve related **IncomingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$IncomingEmailConfiguration**)**
            4. **Commit/Save **$IncomingEmailConfiguration** to Database**
            5. 🔀 **DECISION:** `$EmailAccount/isOutgoingEmailConfigured`
               ➔ **If [true]:**
                  1. **Retrieve related **OutgoingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$OutgoingEmailConfiguration**)**
                  2. **Commit/Save **$OutgoingEmailConfiguration** to Database**
                  3. **Update **$EmailAccount**
      - Set **isOutgoingEmailConfigured** = `true`**
                  4. **Commit/Save **$EmailAccount** to Database**
                  5. **Close current page/popup**
                  6. **Maps to Page: **Email_Connector.EmailConnector_Overview****
                  7. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Retrieve related **OutgoingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$OutgoingEmailConfiguration_2**)**
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.