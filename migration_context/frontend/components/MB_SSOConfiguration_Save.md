# Microflow Detailed Specification: MB_SSOConfiguration_Save

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$SSOConfiguration**
      - Set **IdPMetadataURL** = `if $SSOConfiguration/ReadIdPMetadataFromURL = false then '' else $SSOConfiguration/IdPMetadataURL`**
2. **DB Retrieve **SAML20.SSOConfiguration** Filter: `[Alias = $SSOConfiguration/Alias and id != $SSOConfiguration]` (Result: **$SameAliasSSOConfigurationList**)**
3. **AggregateList**
4. 🔀 **DECISION:** `$SSOCountWithSameAlias > 0`
   ➔ **If [false]:**
      1. **Call Microflow **SAML20.MB_IDP_SelfGenerate_KeyStore****
      2. **Commit/Save **$SSOConfiguration** to Database**
      3. **Call Microflow **SAML20.MB_SPAttributeConsumingServices** (Result: **$IsSPAttributeConsumingServiceSaved**)**
      4. 🔀 **DECISION:** `$IsSPAttributeConsumingServiceSaved`
         ➔ **If [true]:**
            1. **Close current page/popup**
            2. **JavaCallAction**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.