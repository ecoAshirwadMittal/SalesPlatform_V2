# Microflow Detailed Specification: ACT_RevertOffersToSalesReview

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'ReverttoSalesReview'`**
2. **Create Variable **$Description** = `'Revert to Sales Review'`**
3. **JavaCallAction**
4. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      2. **Create Variable **$OfferIDList** = `''`**
      3. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
      4. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus**)**
      5. **Update **$Offer**
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review`
      - Set **OfferRevertedDate** = `[%CurrentDateTime%]`
      - Set **OfferRevertedBy_Account** = `$Account`
      - Set **OfferBeyondSLA** = `false`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus`**
      6. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
      7. **Update Variable **$OfferIDList** = `$Offer/OfferID +' ' +$OfferIDList`**
      8. **Commit/Save **$Offer** to Database**
      9. **Maps to Page: **EcoATM_PWS.PWSOffers****
      10. **Update **$OfferMasterHelper** (and Save to DB)
      - Set **StatusSelected** = `$OfferMasterHelper/StatusSelected`**
      11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.