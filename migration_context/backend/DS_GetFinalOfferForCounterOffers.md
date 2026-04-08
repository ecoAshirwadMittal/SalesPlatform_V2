# Microflow Detailed Specification: DS_GetFinalOfferForCounterOffers

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_PWS.OffersUiHelper** (Result: **$NewOfferStatusAggregate**)
      - Set **HeaderLabel** = `'Final Offer'`
      - Set **HeaderCSSClass** = `'pws-selected-offerstatus-ordered'`
      - Set **TotalSKUs** = `$Offer/FinalOfferTotalSKU`
      - Set **TotalQty** = `$Offer/FinalOfferTotalQty`
      - Set **TotalPrice** = `$Offer/FinalOfferTotalPrice`**
2. 🏁 **END:** Return `$NewOfferStatusAggregate`

**Final Result:** This process concludes by returning a [Object] value.