# Microflow Detailed Specification: DS_GetOfferSummaryTotal

### 📥 Inputs (Parameters)
- **$OfferMasterHelper** (Type: EcoATM_PWS.OfferMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus != empty]` (Result: **$OfferList**)**
3. **AggregateList**
4. **AggregateList**
5. **AggregateList**
6. **AggregateList**
7. **Call Microflow **EcoATM_PWS.SUB_GetOrCreateOfferUiHelper** (Result: **$OffersUiHelper**)**
8. **Update **$OffersUiHelper** (and Save to DB)
      - Set **OfferStatus** = `empty`
      - Set **OfferCount** = `$TotalOffers`
      - Set **TotalSKUs** = `$TotalSKUs`
      - Set **TotalQty** = `$TotalQty`
      - Set **TotalPrice** = `$TotalPrice`
      - Set **OffersUiHelper_OfferMasterHelper** = `$OfferMasterHelper`**
9. 🏁 **END:** Return `$OffersUiHelper`

**Final Result:** This process concludes by returning a [Object] value.