# Microflow Detailed Specification: DS_GetSPMetadata

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **SAML20.SPMetadata**  (Result: **$SPMetadata**)**
2. 🔀 **DECISION:** `$SPMetadata != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$SPMetadata`
   ➔ **If [false]:**
      1. **JavaCallAction**
      2. **Create **SAML20.SPMetadata** (Result: **$NewSPMetadata**)
      - Set **ApplicationURL** = `$ApplicationRootURL`**
      3. 🏁 **END:** Return `$NewSPMetadata`

**Final Result:** This process concludes by returning a [Object] value.