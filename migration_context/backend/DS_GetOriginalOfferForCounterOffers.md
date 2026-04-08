# Microflow Detailed Specification: DS_GetOriginalOfferForCounterOffers

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_PWS.OffersUiHelper** (Result: **$NewOfferStatusAggregate**)
      - Set **HeaderLabel** = `'Original Offer'`
      - Set **TotalSKUs** = `$Offer/OfferSKUCount`
      - Set **TotalQty** = `$Offer/OfferTotalQuantity`
      - Set **TotalPrice** = `$Offer/OfferTotalPrice`**
2. 🏁 **END:** Return `$NewOfferStatusAggregate`

**Final Result:** This process concludes by returning a [Object] value.