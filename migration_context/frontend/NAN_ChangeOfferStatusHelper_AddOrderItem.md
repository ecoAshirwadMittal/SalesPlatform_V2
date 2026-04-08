# Nanoflow: NAN_ChangeOfferStatusHelper_AddOrderItem

**Allowed Roles:** EcoATM_PWS.Administrator

## 📥 Inputs

- **$Order** (EcoATM_PWS.Order)
- **$ChangeOfferStatusHelper** (EcoATM_PWS.ChangeOfferStatusHelper)

## ⚙️ Execution Flow

1. **Update **$ChangeOfferStatusHelper**
      - Set **ChangeOfferStatusHelper_Order** = `$Order`**
2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
