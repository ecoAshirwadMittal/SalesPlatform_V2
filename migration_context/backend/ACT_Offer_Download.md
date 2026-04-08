# Microflow Detailed Specification: ACT_Offer_Download

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      3. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
      4. **Create **EcoATM_PWS.OfferDetailsExport** (Result: **$NewOfferDetailsDownload**)
      - Set **OfferItem_OfferDetailsExport** = `$OfferItemList`
      - Set **DeleteAfterDownload** = `true`**
      5. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='OfferDetails']` (Result: **$MxTemplate**)**
      6. **JavaCallAction**
      7. **Update **$ExcelExport** (and Save to DB)
      - Set **Name** = `$Offer/EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code+'_' +$Offer/OfferID+'.xlsx'`**
      8. **DownloadFile**
      9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.