# Microflow Detailed Specification: ACT_AttributeConsumingService_Save

### 📥 Inputs (Parameters)
- **$SPAttributeConsumingService** (Type: SAML20.SPAttributeConsumingService)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **SAML20.SPAttributeConsumingService** Filter: `[SAML20.SPAttributeConsumingService_SSOConfiguration = $SSOConfiguration and id != $SPAttributeConsumingService/id]` (Result: **$SPAttributeConsumingServiceList**)**
2. **List Operation: **Find** on **$undefined** where `$SPAttributeConsumingService/index` (Result: **$NewSPAttributeConsumingService**)**
3. 🔀 **DECISION:** `$NewSPAttributeConsumingService = empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$SPAttributeConsumingService/isActive = true`
         ➔ **If [false]:**
            1. **Commit/Save **$SPAttributeConsumingService** to Database**
            2. 🏁 **END:** Return `true`
         ➔ **If [true]:**
            1. **Retrieve related **SPRequestedAttribute_SPAttributeConsumingService** via Association from **$SPAttributeConsumingService** (Result: **$SPRequestedAttributeList**)**
            2. 🔀 **DECISION:** `$SPRequestedAttributeList = empty`
               ➔ **If [true]:**
                  1. **ValidationFeedback**
                  2. 🏁 **END:** Return `false`
               ➔ **If [false]:**
                  1. **Commit/Save **$SPAttributeConsumingService** to Database**
                  2. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.