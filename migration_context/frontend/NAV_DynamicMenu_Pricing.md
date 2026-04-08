# Microflow Detailed Specification: NAV_DynamicMenu_Pricing

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `true`
   ➔ **If [true]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_Pricing****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `You do not have access to this page, ask your administrator for beta access.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.