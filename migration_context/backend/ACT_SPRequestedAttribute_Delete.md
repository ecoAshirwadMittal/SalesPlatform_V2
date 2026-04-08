# Microflow Detailed Specification: ACT_SPRequestedAttribute_Delete

### 📥 Inputs (Parameters)
- **$SPRequestedAttribute** (Type: SAML20.SPRequestedAttribute)
- **$SPAttributeConsumingService** (Type: SAML20.SPAttributeConsumingService)
- **$SSOConfiguration** (Type: SAML20.SSOConfiguration)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **SPRequestedAttribute_SPAttributeConsumingService** via Association from **$SPAttributeConsumingService** (Result: **$SPRequestedAttributeList**)**
2. **AggregateList**
3. **Create Variable **$isActive** = `$SPAttributeConsumingService/isActive`**
4. 🔀 **DECISION:** `$AttributeCount <= 1 and $isActive = true`
   ➔ **If [false]:**
      1. **Delete**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.