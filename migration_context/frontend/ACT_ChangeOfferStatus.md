# Nanoflow: ACT_ChangeOfferStatus

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OffersUiHelper** (EcoATM_PWS.OffersUiHelper)

## ⚙️ Execution Flow

1. **Retrieve related **OffersUiHelper_OfferMasterHelper** via Association from **$OffersUiHelper** (Result: **$OfferMasterHelper**)**
2. **Update **$OfferMasterHelper** (and Save to DB)
      - Set **StatusSelected** = `$OffersUiHelper/OfferStatus`**
3. **Call Nanoflow **EcoATM_PWS.ACT_UpdateOfferMasterHelper_HasItems** (Result: **$OfferMasterHelper_2**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
