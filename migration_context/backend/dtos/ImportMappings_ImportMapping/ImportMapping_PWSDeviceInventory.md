# Import Mapping: ImportMapping_PWSDeviceInventory

**JSON Structure:** `EcoATM_PWSMDM.Import_PWSDeviceInventory`

## Mapping Structure

- **JsonObject** (Object) → `EcoATM_PWSMDM.Device`
  - **InventoryItemID** (Value)
    - Attribute: `EcoATM_PWSMDM.Device.DeviceCode`
  - **SKU** (Value)
    - Attribute: `EcoATM_PWSMDM.Device.SKU`
  - **ItemDescription** (Value)
    - Attribute: `EcoATM_PWSMDM.Device.DeviceDescription`
  - **AvailableQuantity** (Value)
    - Attribute: `EcoATM_PWSMDM.Device.AvailableQty`
  - **Brand** (Object) → `EcoATM_PWSMDM.Brand`
    - **Brand** (Value)
      - Attribute: `EcoATM_PWSMDM.Brand.Brand`
  - **Carrier** (Object) → `EcoATM_PWSMDM.Carrier`
    - **Carrier** (Value)
      - Attribute: `EcoATM_PWSMDM.Carrier.Carrier`
  - **Category** (Object) → `EcoATM_PWSMDM.Category`
    - **Category** (Value)
      - Attribute: `EcoATM_PWSMDM.Category.Category`
  - **Condition** (Object) → `EcoATM_PWSMDM.Condition`
    - **Condition** (Value)
      - Attribute: `EcoATM_PWSMDM.Condition.Condition`
  - **DeviceCapacity** (Object) → `EcoATM_PWSMDM.Capacity`
    - **DeviceCapacity** (Value)
      - Attribute: `EcoATM_PWSMDM.Capacity.Capacity`
  - **DeviceColor** (Object) → `EcoATM_PWSMDM.Color`
    - **DeviceColor** (Value)
      - Attribute: `EcoATM_PWSMDM.Color.Color`
  - **DeviceModel** (Object) → `EcoATM_PWSMDM.Model`
    - **DeviceModel** (Value)
      - Attribute: `EcoATM_PWSMDM.Model.Model`
  - **Grade** (Object) → `EcoATM_PWSMDM.Grade`
    - **Grade** (Value)
      - Attribute: `EcoATM_PWSMDM.Grade.Grade`
  - **SearchAttr** (Value)
    - Attribute: `EcoATM_PWSMDM.Device.SearchAttr`
