# Microflow Detailed Specification: MB_ShowSelectedEntityDescriptor

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$SSOConfiguration/SAML20.SSOConfiguration_PreferedEntityDescriptor != empty`
   ➔ **If [true]:**
      1. **Retrieve related **SSOConfiguration_PreferedEntityDescriptor** via Association from **$SSOConfiguration** (Result: **$EntityDescriptor**)**
      2. **Maps to Page: **SAML20.EntityDescriptorDetails_View****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.