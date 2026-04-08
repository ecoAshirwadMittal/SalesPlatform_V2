# Nanoflow: ACT_OpenOfferDetailsPage

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$Offer** (EcoATM_PWS.Offer)

## ⚙️ Execution Flow

1. **Call Microflow **EcoATM_PWS.ACT_GetApplicationURL** (Result: **$AppURL**)**
2. 🔀 **DECISION:** `$Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review`
   ➔ **If [true]:**
      1. **Create Variable **$CompleteURL** = `$AppURL+'/p/offer/'+$Offer/OfferID`**
      2. **Call JS Action **EcoATM_PWS.JS_OpenUrlInNewTab** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create Variable **$CompleteURL_1** = `$AppURL+'/p/offers/'+$Offer/OfferID`**
      2. **Call JS Action **EcoATM_PWS.JS_OpenUrlInNewTab** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
