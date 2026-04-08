# Nanoflow: ACT_OrderDetails_UpdateView

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OrderDetailHelper** (EcoATM_PWS.OrderDetailHelper)
- **$ENUM_OrderDetailsDataGridSource** (Enumeration)

## ⚙️ Execution Flow

1. **Update **$OrderDetailHelper**
      - Set **OrderDetailDataGridSource** = `$ENUM_OrderDetailsDataGridSource`**
2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
