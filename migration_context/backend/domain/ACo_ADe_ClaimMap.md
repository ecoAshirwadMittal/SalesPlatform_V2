# Microflow Detailed Specification: ACo_ADe_ClaimMap

### 📥 Inputs (Parameters)
- **$ClaimMap** (Type: SAML20.ClaimMap)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ClaimMap_SSOConfiguration** via Association from **$ClaimMap** (Result: **$SSOConfiguration**)**
2. **Call Microflow **SAML20.SSOConfigurationReloadConfig_ADe** (Result: **$Variable**)**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.