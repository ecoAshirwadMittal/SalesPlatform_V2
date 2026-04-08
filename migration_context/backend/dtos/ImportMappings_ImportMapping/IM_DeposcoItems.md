# Import Mapping: IM_DeposcoItems

**JSON Structure:** `EcoATM_PWSIntegration.JSON_ATP`

## Mapping Structure

- **ItemInventoryItem** (Object) → `EcoATM_PWSIntegration.ItemInventoryItem_Test`
  - **(parameter)** (Undefined)
    - Attribute: `EcoATM_PWSIntegration.ItemInventoryItem_Test._parameter_`
  - **ItemNumber** (Value)
    - Attribute: `EcoATM_PWSIntegration.ItemInventoryItem_Test.ItemNumber`
  - **FacilityInventoryItem** (Object) → `EcoATM_PWSIntegration.FacilityInventoryItem_Test`
    - **(parameter)** (Undefined)
      - Attribute: `EcoATM_PWSIntegration.FacilityInventoryItem_Test._parameter_`
    - **Inventory** (Object) → `EcoATM_PWSIntegration.Inventory_Test`
      - **(parameter)** (Undefined)
        - Attribute: `EcoATM_PWSIntegration.Inventory_Test._parameter_`
      - **AvailableToPromise** (Value)
        - Attribute: `EcoATM_PWSIntegration.Inventory_Test.AvailableToPromise`
