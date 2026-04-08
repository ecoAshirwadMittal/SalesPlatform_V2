# Nanoflow: DS_CreateOfferDrawerHelper

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## ⚙️ Execution Flow

1. **Create **EcoATM_PWS.OfferDrawerHelper** (Result: **$NewOfferDrawerHelper**)
      - Set **DataGridSource** = `EcoATM_PWS.ENUM_DrawerDataGridSource.ThisSKU`
      - Set **Last90Days** = `addDays([%BeginOfCurrentDay%],-90)`**
2. 🏁 **END:** Return `$NewOfferDrawerHelper`

## 🏁 Returns
`Object`
