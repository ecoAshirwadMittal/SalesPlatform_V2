# Import Mapping: IM_Deposco_OrderHistory_Href

**JSON Structure:** `EcoATM_PWSIntegration.JSON_OrderHistory_Href`

## Mapping Structure

- **Root** (Object) → `EcoATM_PWSIntegration.OrderHistory`
  - **Link** (Object) → `EcoATM_PWSIntegration.Links`
    - **Rel** (Value)
      - Attribute: `EcoATM_PWSIntegration.Links.Rel`
    - **Href** (Value)
      - Attribute: `EcoATM_PWSIntegration.Links.Href`
  - **Datum** (Object) → `EcoATM_PWSIntegration.SKUQuantity`
    - **OrderPackQuantity** (Value)
      - Attribute: `EcoATM_PWSIntegration.SKUQuantity.OrderPackQuantity`
    - **CanceledPackQuantity** (Value)
      - Attribute: `EcoATM_PWSIntegration.SKUQuantity.CanceledPackQuantity`
