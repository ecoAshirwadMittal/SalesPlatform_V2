# Microflow Detailed Specification: MB_SelectAttribute

### 📥 Inputs (Parameters)
- **$Attribute** (Type: SAML20.Attribute)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$SSOConfiguration**
      - Set **SSOConfiguration_Attribute** = `$Attribute`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.