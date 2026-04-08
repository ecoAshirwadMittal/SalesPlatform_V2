# Nanoflow: ACT_SetOfferListDisplayCount

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader

## 📥 Inputs

- **$OfferList** (EcoATM_PWS.Offer)
- **$OrderListSelection** (EcoATM_PWS.OrderListSelection)

## ⚙️ Execution Flow

1. **Aggregate **Count** on **$OfferList** (Result: **$SelectedOfferCount**)**
2. 🔀 **DECISION:** `$SelectedOfferCount>0`
   ➔ **If [true]:**
      1. **Update **$OrderListSelection**
      - Set **SelectedCount** = `$SelectedOfferCount`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$OrderListSelection** (and Save to DB)
      - Set **SelectedCount** = `0`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
