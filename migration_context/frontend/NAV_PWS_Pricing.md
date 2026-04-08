# Nanoflow: NAV_PWS_Pricing

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader

## ⚙️ Execution Flow

1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `true`
   ➔ **If [true]:**
      1. **Open Page: **EcoATM_PWS.PWS_Pricing****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `You do not have access to this page, ask your administrator for beta access.`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
