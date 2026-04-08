# Export Mapping: EM_BuyerUser

**JSON Structure:** `EcoATM_UserManagement.JSON_BuyerUser`

## Mapping Structure

- **Root** (Array)
  - **JsonObject** (Object) → `EcoATM_UserManagement.EcoATMDirectUser`
    - **Email** (Value)
      - Attribute: `Administration.Account.Email`
    - **_changedBy** (Value)
      - Attribute: `EcoATM_UserManagement.EcoATMDirectUser.EntityChanger`
    - **ChangedOn** (Value)
      - Attribute: `System.User.changedDate`
    - **Buyers** (Array)
      - **Buyer** (Object) → `EcoATM_BuyerManagement.Buyer`
        - **CompanyName** (Value)
          - Attribute: `EcoATM_BuyerManagement.Buyer.CompanyName`
