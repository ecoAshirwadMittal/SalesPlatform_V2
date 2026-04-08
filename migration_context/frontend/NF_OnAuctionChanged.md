# Nanoflow: NF_OnAuctionChanged

**Allowed Roles:** EcoATM_BuyerManagement.Administrator, EcoATM_BuyerManagement.SalesOps, EcoATM_BuyerManagement.SalesRep

## 📥 Inputs

- **$QualifiedBuyerCodesQueryHelper** (EcoATM_BuyerManagement.QualifiedBuyerCodesQueryHelper)

## ⚙️ Execution Flow

1. **Update **$QualifiedBuyerCodesQueryHelper**
      - Set **QualifiedBuyerCodesQueryHelper_SchedulingAuction** = `empty`**
2. **Commit/Save **$QualifiedBuyerCodesQueryHelper** to Database**
3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
