# Microflow Detailed Specification: ACT_LDAPConfiguration_ChangeBaseName

### 📥 Inputs (Parameters)
- **$LDAPBaseName** (Type: Email_Connector.LDAPBaseDN)
- **$LDAPConfiguration** (Type: Email_Connector.LDAPConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$LDAPConfiguration**
      - Set **BaseDN** = `$LDAPBaseName/Name`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.