# Microflow Detailed Specification: DS_GetSPAttributeConsumingService_InitialLogin

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SPAttributeConsumingService_SSOConfiguration** via Association from **$SSOConfiguration** (Result: **$SPAttributeConsumingServiceList**)**
2. **List Operation: **Find** on **$undefined** where `SAML20.Enum_Attribute_Consuming_Login_Type.Initial_Login` (Result: **$SPAttributeConsumingServiceIndex**)**
3. 🔀 **DECISION:** `$SPAttributeConsumingServiceIndex= empty`
   ➔ **If [true]:**
      1. **Create **SAML20.SPAttributeConsumingService** (Result: **$NewSPAttributeConsumingService**)
      - Set **ServiceName** = `'Service1'`
      - Set **lang** = `'nl'`
      - Set **isDefault** = `true`
      - Set **SPAttributeConsumingService_SSOConfiguration** = `$SSOConfiguration`
      - Set **index** = `1`
      - Set **LoginType** = `SAML20.Enum_Attribute_Consuming_Login_Type.Initial_Login`**
      2. 🏁 **END:** Return `$NewSPAttributeConsumingService`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$SPAttributeConsumingServiceIndex`

**Final Result:** This process concludes by returning a [Object] value.