# Nanoflow: ACT_DownloadOffers

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OfferMasterHelper** (EcoATM_PWS.OfferMasterHelper)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$ReturnValueName**)**
2. **Call Microflow **EcoATM_PWS.ACT_DownloadOfferExcel****
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
