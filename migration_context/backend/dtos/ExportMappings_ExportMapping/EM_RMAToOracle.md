# Export Mapping: EM_RMAToOracle

**JSON Structure:** `EcoATM_RMA.JSON_RMAToOracle`

## Mapping Structure

- **Root** (Object)
  - **Request** (Object) → `EcoATM_PWSIntegration.RMARequest`
    - **OriginSystemOrderId** (Value)
      - Attribute: `EcoATM_PWSIntegration.RMARequest.OriginSystemOrderId`
    - **OrderType** (Value)
      - Attribute: `EcoATM_PWSIntegration.RMARequest.OrderType`
    - **OrderDate** (Value)
      - Attribute: `EcoATM_PWSIntegration.RMARequest.OrderDate`
    - **BuyerCode** (Value)
      - Attribute: `EcoATM_PWSIntegration.RMARequest.BuyerCode`
    - **OriginSystemUser** (Value)
      - Attribute: `EcoATM_PWSIntegration.RMARequest.OriginSystemUser`
    - **OrderLine** (Array)
      - **OrderLineItem** (Object) → `EcoATM_PWSIntegration.RMALineItem`
        - **Item_number** (Value)
          - Attribute: `EcoATM_PWSIntegration.RMALineItem.ItemNumber`
        - **UnitSellingPrice** (Value)
          - Attribute: `EcoATM_PWSIntegration.RMALineItem.UnitSellingPrice`
        - **RmaOriginalOrder** (Value)
          - Attribute: `EcoATM_PWSIntegration.RMALineItem.RMAOriginalOrder`
        - **RmaIMEI** (Value)
          - Attribute: `EcoATM_PWSIntegration.RMALineItem.RMAIMEI`
        - **RmaReason** (Value)
          - Attribute: `EcoATM_PWSIntegration.RMALineItem.RMAReason`
