# Microflow Detailed Specification: VAL_LDAPConfiguration

### 📥 Inputs (Parameters)
- **$EmailMessage** (Type: Email_Connector.EmailMessage)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailMessage/isEncrypted = true`
   ➔ **If [true]:**
      1. **Retrieve related **EmailMessage_EmailAccount** via Association from **$EmailMessage** (Result: **$EmailAccount**)**
      2. 🔀 **DECISION:** `$EmailAccount/isLDAPConfigured=false`
         ➔ **If [true]:**
            1. **Show Message (Warning): `Please configure email encryption to send encrypted emails.`**
            2. **Maps to Page: **Email_Connector.EmailSecurity_NewEdit****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.