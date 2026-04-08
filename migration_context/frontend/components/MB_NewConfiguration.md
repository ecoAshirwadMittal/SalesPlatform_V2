# Microflow Detailed Specification: MB_NewConfiguration

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **SAML20.DS_GetSPMetadata** (Result: **$SPMetadata**)**
2. **Call Microflow **SAML20.SPMetadata_Validate** (Result: **$Valid**)**
3. 🔀 **DECISION:** `$Valid`
   ➔ **If [false]:**
      1. **Show Message (Information): `Please make sure the SP Metadata is valid before adding an IdP configuration`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Create **SAML20.SSOConfiguration** (Result: **$NewSSOConfiguration**)
      - Set **MigratedToPrioritizedSAMLAuthnContexts** = `true`**
      2. **DB Retrieve **MxModelReflection.Module** Filter: `[SynchronizeObjectsWithinModule = true()]` (Result: **$ModuleList**)**
      3. **AggregateList**
      4. 🔀 **DECISION:** `$count > 0`
         ➔ **If [false]:**
            1. **Show Message (Information): `In this configuration it's required to select some entities which are presented by the Model reflection module. Please synchronize objects first, using this module.`**
            2. **Maps to Page: **SAML20.SSOConfig_Step0****
            3. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Maps to Page: **SAML20.SSOConfig_Step2****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.