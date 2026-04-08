# Microflow Detailed Specification: DS_GetOfferSummaryByStatus_Decline

### 📥 Inputs (Parameters)
- **$OfferMasterHelper** (Type: EcoATM_PWS.OfferMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus = 'Declined']` (Result: **$DeclinedOfferList**)**
3. **AggregateList**
4. **AggregateList**
5. **AggregateList**
6. **AggregateList**
7. **Call Microflow **EcoATM_PWS.SUB_GetOrCreateOfferUiHelper** (Result: **$OffersUiHelper**)**
8. **Update **$OffersUiHelper** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Declined`
      - Set **TotalSKUs** = `$TotalSKU`
      - Set **TotalQty** = `round($TotalQuantity)`
      - Set **TotalPrice** = `round($TotalPrice)`
      - Set **OffersUiHelper_OfferMasterHelper** = `$OfferMasterHelper`
      - Set **OfferCount** = `$Count`**
9. **Call Microflow **Custom_Logging.SUB_Log_Info****
10. 🏁 **END:** Return `$OffersUiHelper`

**Final Result:** This process concludes by returning a [Object] value.