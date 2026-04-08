# Microflow Detailed Specification: ACT_EmailAccount_SaveEmailSecurityConfiguration

### 📥 Inputs (Parameters)
- **$EmailAccount** (Type: Email_Connector.EmailAccount)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$EmailAccount/isP12Configured`
   ➔ **If [false]:**
      1. **Update **$EmailAccount**
      - Set **Pk12Certificate_EmailAccount** = `empty`**
      2. 🔀 **DECISION:** `$EmailAccount/isLDAPConfigured`
         ➔ **If [true]:**
            1. **Retrieve related **EmailAccount_LDAPConfiguration** via Association from **$EmailAccount** (Result: **$LDAPConfiguration**)**
            2. 🔀 **DECISION:** `$LDAPConfiguration/BaseDN=empty`
               ➔ **If [true]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$LDAPConfiguration** (and Save to DB)
      - Set **EmailAccount_LDAPConfiguration** = `$EmailAccount`**
                  2. **Commit/Save **$EmailAccount** to Database**
                  3. **Close current page/popup**
                  4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$EmailAccount**
      - Set **EmailAccount_LDAPConfiguration** = `empty`**
            2. **Commit/Save **$EmailAccount** to Database**
            3. **Close current page/popup**
            4. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$EmailAccount/Email_Connector.Pk12Certificate_EmailAccount/Email_Connector.Pk12Certificate/HasContents`
         ➔ **If [true]:**
            1. **Retrieve related **Pk12Certificate_EmailAccount** via Association from **$EmailAccount** (Result: **$Pk12Certificate**)**
            2. **Update **$Pk12Certificate** (and Save to DB)
      - Set **Pk12Certificate_EmailAccount** = `$EmailAccount`**
            3. **Commit/Save **$EmailAccount** to Database**
            4. **Close current page/popup**
            5. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.