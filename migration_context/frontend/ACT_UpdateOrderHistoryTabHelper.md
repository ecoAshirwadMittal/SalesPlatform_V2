# Nanoflow: ACT_UpdateOrderHistoryTabHelper

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OrderHistoryHelper** (EcoATM_PWS.OrderHistoryHelper)
- **$ENUM_OrderHistoryTab** (Enumeration)

## ⚙️ Execution Flow

1. **Update **$OrderHistoryHelper**
      - Set **CurrentTab** = `$ENUM_OrderHistoryTab`**
2. 🏁 **END:** Return `$OrderHistoryHelper`

## 🏁 Returns
`Object`
