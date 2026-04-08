# Nanoflow: NF_OnIncludedChanged

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep

## 📥 Inputs

- **$QualifiedBuyerCodes** (EcoATM_BuyerManagement.QualifiedBuyerCodes)

## ⚙️ Execution Flow

1. **DB Retrieve **EcoATM_BuyerManagement.AuctionsFeature**  (Result: **$AuctionsFeature**)**
2. 🔀 **DECISION:** `$AuctionsFeature/LegacyManualQualification`
   ➔ **If [true]:**
      1. **Call Nanoflow **EcoATM_BuyerManagement.NF_OnIncludedChanged_Legacy****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Nanoflow **EcoATM_BuyerManagement.NF_OnIncludedChanged_New** (Result: **$SchedulingAuction**)**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
