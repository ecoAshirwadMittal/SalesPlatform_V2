# Import Mapping: IM_DeposcoATP_XML

## Mapping Structure

- **itemInventory** (Object) → `EcoATM_PWSIntegration.ItemInventoryItem`
  - **(parameter)** (Undefined)
    - Attribute: `EcoATM_PWSIntegration.ItemInventoryItem.PageNo`
  - **itemNumber** (Value)
    - Attribute: `EcoATM_PWSIntegration.ItemInventoryItem.ItemNumber`
  - **facilityInventory** (Object) → `EcoATM_PWSIntegration.FacilityInventoryItem`
    - **(parameter)** (Undefined)
      - Attribute: `-`
    - **facility** (Value)
      - Attribute: `EcoATM_PWSIntegration.FacilityInventoryItem.Facility`
    - **inventory** (Object) → `EcoATM_PWSIntegration.Inventory`
      - **(parameter)** (Undefined)
        - Attribute: `-`
      - **total** (Value)
        - Attribute: `EcoATM_PWSIntegration.Inventory.Total`
      - **availableToPromise** (Value)
        - Attribute: `EcoATM_PWSIntegration.Inventory.AvailableToPromise`
      - **unallocated** (Value)
        - Attribute: `EcoATM_PWSIntegration.Inventory.Unallocated`
      - **allocated** (Value)
        - Attribute: `EcoATM_PWSIntegration.Inventory.Allocated`
