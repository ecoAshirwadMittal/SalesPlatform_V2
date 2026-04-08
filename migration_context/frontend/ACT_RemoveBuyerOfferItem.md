# Nanoflow: ACT_RemoveBuyerOfferItem

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep, EcoATM_PWS.SalesLeader

## 📥 Inputs

- **$BuyerOfferItem** (EcoATM_PWS.BuyerOfferItem)
- **$BuyerOffer** (EcoATM_PWS.BuyerOffer)

## ⚙️ Execution Flow

1. **Update **$BuyerOffer**
      - Set **CSSClass** = `''`**
2. **Call Microflow **EcoATM_PWS.SUB_RemoveBuyerOfferItem****
3. **Update **$BuyerOffer**
      - Set **CSSClass** = `'pws-font-animation'`**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
