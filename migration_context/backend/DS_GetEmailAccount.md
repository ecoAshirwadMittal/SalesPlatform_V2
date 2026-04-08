# Microflow Detailed Specification: DS_GetEmailAccount

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **Email_Connector.EmailAccount**  (Result: **$EmailAccount**)**
2. 🔀 **DECISION:** `$EmailAccount=empty`
   ➔ **If [false]:**
      1. **Retrieve related **IncomingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$IncomingEmailConfiguration**)**
      2. 🔀 **DECISION:** `$IncomingEmailConfiguration=empty`
         ➔ **If [true]:**
            1. **Create **Email_Connector.IncomingEmailConfiguration** (Result: **$NewIncomingEmailConfiguration**)**
            2. **Update **$EmailAccount**
      - Set **IncomingEmailConfiguration_EmailAccount** = `$NewIncomingEmailConfiguration`**
            3. **Retrieve related **OutgoingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$OutgoingEmailConfiguration**)**
            4. 🔀 **DECISION:** `$OutgoingEmailConfiguration=empty`
               ➔ **If [true]:**
                  1. **Create **Email_Connector.OutgoingEmailConfiguration** (Result: **$NewOutgoingEmailConfiguration**)**
                  2. **Update **$EmailAccount**
      - Set **OutgoingEmailConfiguration_EmailAccount** = `$NewOutgoingEmailConfiguration`**
                  3. 🏁 **END:** Return `$EmailAccount`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `$EmailAccount`
         ➔ **If [false]:**
            1. **Retrieve related **OutgoingEmailConfiguration_EmailAccount** via Association from **$EmailAccount** (Result: **$OutgoingEmailConfiguration**)**
            2. 🔀 **DECISION:** `$OutgoingEmailConfiguration=empty`
               ➔ **If [true]:**
                  1. **Create **Email_Connector.OutgoingEmailConfiguration** (Result: **$NewOutgoingEmailConfiguration**)**
                  2. **Update **$EmailAccount**
      - Set **OutgoingEmailConfiguration_EmailAccount** = `$NewOutgoingEmailConfiguration`**
                  3. 🏁 **END:** Return `$EmailAccount`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `$EmailAccount`
   ➔ **If [true]:**
      1. **Create **Email_Connector.IncomingEmailConfiguration** (Result: **$NewIncomingEmailConfiguration_1**)**
      2. **Create **Email_Connector.OutgoingEmailConfiguration** (Result: **$NewOutgoingEmailConfiguration_1**)**
      3. **Create **Email_Connector.EmailAccount** (Result: **$NewEmailAccount**)
      - Set **OutgoingEmailConfiguration_EmailAccount** = `$NewOutgoingEmailConfiguration_1`
      - Set **IncomingEmailConfiguration_EmailAccount** = `$NewIncomingEmailConfiguration_1`**
      4. 🏁 **END:** Return `$NewEmailAccount`

**Final Result:** This process concludes by returning a [Object] value.