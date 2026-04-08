# Microflow Detailed Specification: MB_ToggleActive

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$SSOConfiguration** (and Save to DB)
      - Set **Active** = `not($SSOConfiguration/Active)`**
2. **JavaCallAction**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.