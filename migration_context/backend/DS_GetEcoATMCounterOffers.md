# Microflow Detailed Specification: DS_GetEcoATMCounterOffers

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_PWS.OffersUiHelper** (Result: **$NewOfferStatusAggregate**)
      - Set **HeaderLabel** = `'EcoATMs Counter Offer'`
      - Set **TotalSKUs** = `$Offer/CounterOfferTotalSKU`
      - Set **TotalQty** = `$Offer/CounterOfferTotalQty`
      - Set **TotalPrice** = `$Offer/CounterOfferTotalPrice`**
2. 🏁 **END:** Return `$NewOfferStatusAggregate`

**Final Result:** This process concludes by returning a [Object] value.