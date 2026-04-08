# Microflow Detailed Specification: SUB_RetrieveShipmentDetails

### 📥 Inputs (Parameters)
- **$Auth** (Type: Variable)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)
- **$ShipmentAPI** (Type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$Order** (Type: EcoATM_PWS.Order)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$ShipmentURL** = `$DeposcoConfig/BaseURL + $ShipmentAPI/ServiceUrl+'?orderHeaders.number='+$Order/OrderNumber`**
2. **RestCall**
3. **Call Microflow **EcoATM_PWSIntegration.ACT_AuditRestAPICalls****
4. **ImportXml**
5. **List Operation: **Head** on **$undefined** (Result: **$HeadShipment**)**
6. 🔄 **LOOP:** For each **$IteratorShipment** in **$ListOfShipments**
   │ 1. **Retrieve related **Line_Shipment** via Association from **$IteratorShipment** (Result: **$IteratorLineList**)**
   │ 2. **Retrieve related **Line_Shipment** via Association from **$HeadShipment** (Result: **$LineList_Shipment**)**
   │ 3. **List Operation: **Union** on **$undefined** (Result: **$LineList_Aggregated**)**
   │ 4. **Update **$HeadShipment**
      - Set **Line_Shipment** = `$LineList_Aggregated`**
   └─ **End Loop**
7. 🏁 **END:** Return `$HeadShipment`

**Final Result:** This process concludes by returning a [Object] value.