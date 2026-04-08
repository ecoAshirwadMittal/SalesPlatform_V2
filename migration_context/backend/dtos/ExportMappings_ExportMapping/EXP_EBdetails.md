# Export Mapping: EXP_EBdetails

**JSON Structure:** `EcoATM_DA.JSON_EBChanges`

## Mapping Structure

- **Root** (Array) → `Integration.Root`
  - **JsonObject** (Object) → `Integration.JsonObject`
    - **Week** (Value)
      - Attribute: `Integration.JsonObject.Week`
    - **Year** (Value)
      - Attribute: `Integration.JsonObject.Year`
    - **EB** (Value)
      - Attribute: `Integration.JsonObject.EB`
    - **ProductID** (Value)
      - Attribute: `Integration.JsonObject.ProductID`
    - **Grade** (Value)
      - Attribute: `Integration.JsonObject.Grade`
    - **Buyercode** (Array) → `Integration.Buyercode`
      - **BuyercodeItem** (Wrapper) → `Integration.BuyercodeItem`
        - **Value** (Value)
          - Attribute: `Integration.BuyercodeItem.Value`
