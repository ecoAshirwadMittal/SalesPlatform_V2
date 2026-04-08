# Nanoflow: ACT_Offers_Download_SimilarSKU

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OfferDrawerHelper** (EcoATM_PWS.OfferDrawerHelper)
- **$OfferItem** (EcoATM_PWS.OfferItem)

## ⚙️ Execution Flow

1. **Create Variable **$TimerName** = `'OfferReviewSimilarSKUDownload'`**
2. **Create Variable **$Description** = `'Offers Download for Similar SKU'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
5. **Call Microflow **EcoATM_PWS.SUB_Offers_Download_SimilarSKU** (Result: **$Success**)**
6. 🔀 **DECISION:** `$Success`
   ➔ **If [true]:**
      1. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      2. **Show Message (Error): `We hit an error downloading the listing. Please try again.`**
      3. **Call Microflow **Custom_Logging.SUB_Log_Error****
      4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
