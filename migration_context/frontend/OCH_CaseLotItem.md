# Nanoflow: OCH_CaseLotItem

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$BuyerOfferItem** (EcoATM_PWS.BuyerOfferItem)
- **$BuyerOffer** (EcoATM_PWS.BuyerOffer)
- **$CaseLot** (EcoATM_PWSMDM.CaseLot)

## ⚙️ Execution Flow

1. **Update **$BuyerOfferItem**
      - Set **CSSClass** = `''`**
2. **Update **$BuyerOffer**
      - Set **CSSClass** = `''`**
3. **Call Microflow **EcoATM_PWS.OCH_CaseLotItem_Inventory****
4. **Update **$BuyerOfferItem**
      - Set **CSSClass** = `'pws-font-animation'`**
5. **Update **$BuyerOffer**
      - Set **CSSClass** = `'pws-font-animation'`**
6. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
