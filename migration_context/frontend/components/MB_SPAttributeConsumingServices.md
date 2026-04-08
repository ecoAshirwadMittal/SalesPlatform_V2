# Microflow Detailed Specification: MB_SPAttributeConsumingServices

### 📥 Inputs (Parameters)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SPAttributeConsumingService_SSOConfiguration** via Association from **$SSOConfiguration** (Result: **$SPAttributeConsumingServiceList**)**
2. **List Operation: **Find** on **$undefined** where `SAML20.Enum_Attribute_Consuming_Login_Type.Initial_Login` (Result: **$SPAttributeConsumingServiceInitialLogin**)**
3. **Call Microflow **SAML20.ACT_AttributeConsumingService_Save** (Result: **$SPAttributeConsumingServiceInitialLoginSaved**)**
4. **List Operation: **Find** on **$undefined** where `SAML20.Enum_Attribute_Consuming_Login_Type.InSession_Login` (Result: **$SPAttributeConsumingServiceInSession**)**
5. **Call Microflow **SAML20.ACT_AttributeConsumingService_Save** (Result: **$SPAttributeConsumingServiceInSessionSaved**)**
6. 🔀 **DECISION:** `$SPAttributeConsumingServiceInitialLoginSaved and $SPAttributeConsumingServiceInSessionSaved`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.