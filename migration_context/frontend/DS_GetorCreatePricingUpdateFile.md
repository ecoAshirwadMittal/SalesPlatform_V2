# Nanoflow: DS_GetorCreatePricingUpdateFile

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader

## 📥 Inputs

- **$MDMFuturePriceHelper** (EcoATM_PWS.MDMFuturePriceHelper)

## ⚙️ Execution Flow

1. **Retrieve related **PricingUpdateFile_MDMFuturePriceHelper** via Association from **$MDMFuturePriceHelper** (Result: **$PricingUpdateFile**)**
2. 🔀 **DECISION:** `$PricingUpdateFile != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$PricingUpdateFile`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.PricingUpdateFile** (Result: **$NewPricingUpdateFile**)
      - Set **PricingUpdateFile_MDMFuturePriceHelper** = `$MDMFuturePriceHelper`**
      2. 🏁 **END:** Return `$NewPricingUpdateFile`

## 🏁 Returns
`Object`
