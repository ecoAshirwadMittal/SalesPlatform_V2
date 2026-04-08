# Nanoflow: NAV_PWS_Offers

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$InProgress**)**
2. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
3. 🔀 **DECISION:** `$FeatureFlagState`
   ➔ **If [false]:**
      1. **Show Message (Information): `You do not have access to this page, ask your administrator for beta access.`**
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.SUB_NAV_ShowPWSOffersPage****
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty

## ⚠️ Error Handling

- On error in **Call Microflow **EcoATM_PWS.SUB_NAV_ShowPWSOffersPage**** → Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)

## 🏁 Returns
`Void`
