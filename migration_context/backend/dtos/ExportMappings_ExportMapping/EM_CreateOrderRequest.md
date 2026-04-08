# Export Mapping: EM_CreateOrderRequest

**JSON Structure:** `EcoATM_PWS.JSON_CreateOrderRequest`

## Mapping Structure

- **Root** (Object)
  - **Request** (Object) → `EcoATM_PWSIntegration.OrderRequest`
    - **OriginSystemOrderId** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.OriginSystemOrderId`
    - **OrderType** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.OrderType`
    - **OrderDate** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.OrderDate`
    - **BuyerCode** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.BuyerCode`
    - **OriginSystemUser** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.OriginationSystemUser`
    - **ShippingInstructions** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.ShippingInstructions`
    - **FreeFormOrd01** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.FreeFormOrd01`
    - **FreeFormOrd02** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.FreeFormOrd02`
    - **FreeFormOrd03** (Value)
      - Attribute: `EcoATM_PWSIntegration.OrderRequest.FreeFormOrd03`
    - **OrderLine** (Array)
      - **OrderLineItem** (Object) → `EcoATM_PWSIntegration.OrderLineItem`
        - **Item_number** (Value)
          - Attribute: `EcoATM_PWSIntegration.OrderLineItem.Item_number`
        - **Quantity** (Value)
          - Attribute: `EcoATM_PWSIntegration.OrderLineItem.Quantity`
        - **UnitSellingPrice** (Value)
          - Attribute: `EcoATM_PWSIntegration.OrderLineItem.UnitSellingPrice`
        - **OriginSystemLineId** (Value)
          - Attribute: `EcoATM_PWSIntegration.OrderLineItem.OriginSystemLineId`
        - **FreeFormLine01** (Value)
          - Attribute: `EcoATM_PWSIntegration.OrderLineItem.FreeFormLine01`
        - **FreeFormLine02** (Value)
          - Attribute: `EcoATM_PWSIntegration.OrderLineItem.FreeFormLine02`
        - **FreeFormLine03** (Value)
          - Attribute: `EcoATM_PWSIntegration.OrderLineItem.FreeFormLine03`
