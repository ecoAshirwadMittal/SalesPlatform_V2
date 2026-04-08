# Microflow Detailed Specification: MB_SaveSPMetadata

### 📥 Inputs (Parameters)
- **$SPMetadata** (Type: SAML20.SPMetadata)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$SPMetadata/DoesEntityIdDifferFromAppURL`
   ➔ **If [false]:**
      1. **Update **$SPMetadata**
      - Set **EntityID** = `$SPMetadata/ApplicationURL`**
      2. **Call Microflow **SAML20.SPMetadata_Validate** (Result: **$Valid**)**
      3. 🔀 **DECISION:** `$Valid`
         ➔ **If [true]:**
            1. **Commit/Save **$SPMetadata** to Database**
            2. **JavaCallAction**
            3. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **Call Microflow **SAML20.SPMetadata_Validate** (Result: **$Valid**)**
      2. 🔀 **DECISION:** `$Valid`
         ➔ **If [true]:**
            1. **Commit/Save **$SPMetadata** to Database**
            2. **JavaCallAction**
            3. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.