# Nanoflow: SUB_GetCurrentUser

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.Bidder, EcoATM_PWS.SalesLeader, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## ⚙️ Execution Flow

1. **DB Retrieve **Administration.Account** Filter: `[id = '[%CurrentUser%]']` (Result: **$CurrentLoggedInUser**)**
2. 🏁 **END:** Return `$CurrentLoggedInUser`

## 🏁 Returns
`Object`
