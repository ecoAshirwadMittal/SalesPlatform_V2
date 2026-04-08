# Nanoflow: ACT_SaveCounterOffer_Drawer

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OfferItem** (EcoATM_PWS.OfferItem)

## ⚙️ Execution Flow

1. **Call Microflow **EcoATM_PWS.OCH_OfferItem_Quantity****
2. **Commit/Save **$OfferItem** to Database**
3. **Close current page/popup**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
