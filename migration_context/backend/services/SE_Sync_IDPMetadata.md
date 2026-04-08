# Microflow Detailed Specification: SE_Sync_IDPMetadata

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **SAML20.SSOConfiguration** Filter: `[ReadIdPMetadataFromURL=true()] [Active=true()]` (Result: **$SSOConfiguration**)**
2. 🔄 **LOOP:** For each **$IteratorSSOConfiguration** in **$SSOConfiguration**
   │ 1. **Call Microflow **SAML20.IdPMetadata_Refresh** (Result: **$Variable**)**
   └─ **End Loop**
3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.