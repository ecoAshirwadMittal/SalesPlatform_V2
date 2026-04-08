# Microflow Detailed Specification: MB_SelectEntityDescriptor

### 📥 Inputs (Parameters)
- **$EntityDescriptor** (Type: SAML20.EntityDescriptor)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$SSOConfiguration** (and Save to DB)
      - Set **SSOConfiguration_PreferedEntityDescriptor** = `$EntityDescriptor`**
2. **Close current page/popup**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.