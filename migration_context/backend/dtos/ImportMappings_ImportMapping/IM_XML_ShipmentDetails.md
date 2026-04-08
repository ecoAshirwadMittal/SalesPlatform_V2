# Import Mapping: IM_XML_ShipmentDetails

## Mapping Structure

- **shipment** (Object) → `EcoATM_PWSIntegration.Shipment`
  - **trackingNumber** (Value)
    - Attribute: `EcoATM_PWSIntegration.Shipment.TrackingNumber`
  - **trackingUrl** (Value)
    - Attribute: `EcoATM_PWSIntegration.Shipment.TrackingUrl`
  - **line** (Object) → `EcoATM_PWSIntegration.Line`
    - **itemNumber** (Value)
      - Attribute: `EcoATM_PWSIntegration.Line.ItemNumber`
    - **serialNumber** (Value)
      - Attribute: `EcoATM_PWSIntegration.Line.SerialNumber`
    - **trackingNumber** (Value)
      - Attribute: `EcoATM_PWSIntegration.Line.TrackingNumber`
    - **inventoryAttribute1** (Value)
      - Attribute: `EcoATM_PWSIntegration.Line.IMEINumber`
    - **lpn** (Value)
      - Attribute: `EcoATM_PWSIntegration.Line.BoxLPNNumber`
