# Export Mapping: EXP_Buyer

**JSON Structure:** `EcoATM_MDM.JSON_Buyer`

## Mapping Structure

- **Root** (Array)
  - **JsonObject** (Object) → `EcoATM_BuyerManagement.Buyer`
    - **SubmissionID** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.SubmissionID`
    - **CompanyName** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.CompanyName`
    - **Status** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.Status`
    - **BuyerCodesDisplay** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.BuyerCodesDisplay`
    - **IsFailedBuyerDisable** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.isFailedBuyerDisable`
    - **CreatedBy** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.EntityOwner`
    - **_ChangedBy** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.EntityChanger`
    - **CreatedOn** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.createdDate`
    - **ChangedOn** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.changedDate`
    - **IsSpecialBuyer** (Value)
      - Attribute: `EcoATM_BuyerManagement.Buyer.isSpecialBuyer`
    - **SalesRep** (Object) → `EcoATM_BuyerManagement.SalesRepresentative`
      - **FirstName** (Value)
        - Attribute: `EcoATM_BuyerManagement.SalesRepresentative.SalesRepFirstName`
      - **LastName** (Value)
        - Attribute: `EcoATM_BuyerManagement.SalesRepresentative.SalesRepLastName`
      - **SalesRepId** (Value)
        - Attribute: `EcoATM_BuyerManagement.SalesRepresentative.SalesRepresentativeId`
    - **Codes** (Array)
      - **Code** (Object) → `EcoATM_BuyerManagement.BuyerCode`
        - **BuyerCodeType** (Value)
          - Attribute: `EcoATM_BuyerManagement.BuyerCode.BuyerCodeType`
        - **Code** (Value)
          - Attribute: `EcoATM_BuyerManagement.BuyerCode.Code`
        - **Status** (Value)
          - Attribute: `EcoATM_BuyerManagement.BuyerCode.Status`
        - **Budget** (Value)
          - Attribute: `EcoATM_BuyerManagement.BuyerCode.Budget`
        - **CreatedBy** (Value)
          - Attribute: `EcoATM_BuyerManagement.BuyerCode.EntityOwner`
        - **_ChangedBy** (Value)
          - Attribute: `EcoATM_BuyerManagement.BuyerCode.EntityChanger`
        - **CreatedOn** (Value)
          - Attribute: `EcoATM_BuyerManagement.BuyerCode.createdDate`
        - **ChangedOn** (Value)
          - Attribute: `EcoATM_BuyerManagement.BuyerCode.changedDate`
