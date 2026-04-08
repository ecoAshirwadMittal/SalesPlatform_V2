# Import Mapping: IM_Deposco_OrderHistory

**JSON Structure:** `EcoATM_PWSIntegration.JSON_OrderHistory`

## Mapping Structure

- **Datum** (Object) → `EcoATM_PWSIntegration.OrderHistory`
  - **Number** (Value)
    - Attribute: `EcoATM_PWSIntegration.OrderHistory.Number`
  - **CustomerOrderNumber** (Value)
    - Attribute: `EcoATM_PWSIntegration.OrderHistory.CustomerOrderNumber`
  - **ShipToContact** (Object) → `EcoATM_PWSIntegration.ShipToContact`
    - **FirstName** (Value)
      - Attribute: `EcoATM_PWSIntegration.ShipToContact.FirstName`
  - **ShipToAddress** (Object) → `EcoATM_PWSIntegration.ShipToAddress`
    - **Line3** (Value)
      - Attribute: `EcoATM_PWSIntegration.ShipToAddress.Line3`
  - **ActualShipDate** (Value)
    - Attribute: `EcoATM_PWSIntegration.OrderHistory.ActualShipDate`
  - **ShipVia** (Value)
    - Attribute: `EcoATM_PWSIntegration.OrderHistory.ShipVia`
  - **ShipVendor** (Value)
    - Attribute: `EcoATM_PWSIntegration.OrderHistory.ShipVendor`
  - **OrderStatus** (Value)
    - Attribute: `EcoATM_PWSIntegration.OrderHistory.OrderStatus`
  - **Link** (Object) → `EcoATM_PWSIntegration.Links`
    - **Rel** (Value)
      - Attribute: `EcoATM_PWSIntegration.Links.Rel`
    - **Href** (Value)
      - Attribute: `EcoATM_PWSIntegration.Links.Href`
  - **Data_2Item** (Object) → `EcoATM_PWSIntegration.SKUQuantity`
    - **OrderPackQuantity** (Value)
      - Attribute: `EcoATM_PWSIntegration.SKUQuantity.OrderPackQuantity`
    - **CanceledPackQuantity** (Value)
      - Attribute: `EcoATM_PWSIntegration.SKUQuantity.CanceledPackQuantity`
