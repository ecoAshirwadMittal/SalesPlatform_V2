# Microflow Detailed Specification: SUB_UpdateShipmentData

### 📥 Inputs (Parameters)
- **$Order** (Type: EcoATM_PWS.Order)
- **$OrderHistory** (Type: EcoATM_PWSIntegration.OrderHistory)
- **$DesposcoAPIsList** (Type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$Auth** (Type: Variable)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **ShipmentDetail_Order** via Association from **$Order** (Result: **$ShipmentDetailList_Existing**)**
2. **Delete**
3. **Retrieve related **OfferItem_Order** via Association from **$Order** (Result: **$OfferItemList**)**
4. **CreateList**
5. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Update **$IteratorOfferItem**
      - Set **ShippedQty** = `0`**
   │ 2. **Retrieve related **IMEIDetail_OfferItem** via Association from **$IteratorOfferItem** (Result: **$IMEIDetailList_Existing**)**
   │ 3. **Delete**
   └─ **End Loop**
6. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/ServiceName= EcoATM_PWSIntegration.ENUM_DeposcoServices.Shipment` (Result: **$ShipmentAPI**)**
7. **Retrieve related **ShipToAddress_OrderHistory** via Association from **$OrderHistory** (Result: **$ShipToAddress**)**
8. **Retrieve related **ShipToContact_OrderHistory** via Association from **$OrderHistory** (Result: **$ShipToContact**)**
9. **Retrieve related **Shipmentdata_OrderHistory** via Association from **$OrderHistory** (Result: **$ShipmentdataList**)**
10. **Retrieve related **ShipmentDetail_Order** via Association from **$Order** (Result: **$ShipmentDetailList**)**
11. **Call Microflow **EcoATM_PWSIntegration.SUB_RetrieveShipmentDetails** (Result: **$Shipment**)**
12. 🔀 **DECISION:** `$Shipment!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **Line_Shipment** via Association from **$Shipment** (Result: **$LineList**)**
      2. 🔄 **LOOP:** For each **$IteratorLineData** in **$LineList**
         │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/TrackingNumber=$IteratorLineData/TrackingNumber` (Result: **$ShipmentDetailExists**)**
         │ 2. 🔀 **DECISION:** `$ShipmentDetailExists!=empty`
         │    ➔ **If [true]:**
         │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU = $IteratorLineData/ItemNumber` (Result: **$OfferItem_Line**)**
         │       2. 🔀 **DECISION:** `$OfferItem_Line!=empty`
         │          ➔ **If [true]:**
         │             1. **Retrieve related **OfferItem_ShipmentDetail** via Association from **$ShipmentDetailExists** (Result: **$OfferItemList_AlreadyMapped**)**
         │             2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject=$OfferItem_Line` (Result: **$OfferItem_Alreadymapped**)**
         │             3. **Update **$OfferItem_Line**
      - Set **ShippedQty** = `if($OfferItem_Line/ShippedQty=empty) then 1 else $OfferItem_Line/ShippedQty+1`
      - Set **ShippedPrice** = `if($OfferItem_Line/ShippedQty!=empty) then $OfferItem_Line/ShippedPrice+$OfferItem_Line/FinalOfferPrice else $OfferItem_Line/ShippedPrice`
      - Set **OfferItem_ShipmentDetail** = `$ShipmentDetailExists`**
         │             4. **Update **$ShipmentDetailExists**
      - Set **Quantity** = `$ShipmentDetailExists/Quantity+1`
      - Set **SKUCount** = `if($OfferItem_Alreadymapped!=empty) then $ShipmentDetailExists/SKUCount else $ShipmentDetailExists/SKUCount+1`**
         │             5. **Create **EcoATM_PWS.IMEIDetail** (Result: **$NewIMEIDetail_1**)
      - Set **IMEIDetail_OfferItem** = `$OfferItem_Line`
      - Set **IMEINumber** = `$IteratorLineData/IMEINumber`
      - Set **IMEIDetail_ShipmentDetail** = `$ShipmentDetailExists`
      - Set **SerialNumber** = `$IteratorLineData/SerialNumber`
      - Set **BoxLPNNumber** = `$IteratorLineData/BoxLPNNumber`**
         │             6. **Add **$$NewIMEIDetail_1
** to/from list **$IMEIDetailList****
         │          ➔ **If [false]:**
         │    ➔ **If [false]:**
         │       1. **Create **EcoATM_PWS.ShipmentDetail** (Result: **$NewShipmentDetail**)
      - Set **TrackingNumber** = `$IteratorLineData/TrackingNumber`
      - Set **TrackingUrl** = `if($Shipment/TrackingUrl!=empty and $Shipment/TrackingNumber!=empty and $IteratorLineData/TrackingNumber!=empty) then replaceAll($Shipment/TrackingUrl,$Shipment/TrackingNumber, $IteratorLineData/TrackingNumber) else empty`
      - Set **ShipmentDetail_Order** = `$Order`
      - Set **Quantity** = `1`
      - Set **SKUCount** = `1`**
         │       2. **Add **$$NewShipmentDetail
** to/from list **$ShipmentDetailList****
         │       3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU = $IteratorLineData/ItemNumber` (Result: **$OfferItem_Line_1**)**
         │       4. 🔀 **DECISION:** `$OfferItem_Line_1!=empty`
         │          ➔ **If [true]:**
         │             1. **Update **$OfferItem_Line_1**
      - Set **ShippedQty** = `if($OfferItem_Line_1/ShippedQty=empty) then 1 else $OfferItem_Line_1/ShippedQty+1`
      - Set **ShippedPrice** = `if($OfferItem_Line_1/ShippedQty!=empty) then $OfferItem_Line_1/ShippedPrice+$OfferItem_Line_1/FinalOfferPrice else $OfferItem_Line_1/ShippedPrice`
      - Set **OfferItem_ShipmentDetail** = `$NewShipmentDetail`**
         │             2. **Create **EcoATM_PWS.IMEIDetail** (Result: **$NewIMEIDetail**)
      - Set **IMEIDetail_OfferItem** = `$OfferItem_Line_1`
      - Set **IMEINumber** = `$IteratorLineData/IMEINumber`
      - Set **IMEIDetail_ShipmentDetail** = `$NewShipmentDetail`
      - Set **SerialNumber** = `$IteratorLineData/SerialNumber`
      - Set **BoxLPNNumber** = `$IteratorLineData/BoxLPNNumber`**
         │             3. **Add **$$NewIMEIDetail
** to/from list **$IMEIDetailList****
         │          ➔ **If [false]:**
         └─ **End Loop**
      3. **AggregateList**
      4. **AggregateList**
      5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/ShippedQty>0` (Result: **$ShippedOfferItems**)**
      6. **AggregateList**
      7. **DB Retrieve **EcoATM_PWS.OrderStatus** Filter: `[SystemStatus='Ship Complete']` (Result: **$ShipCompleteStatus**)**
      8. **Update **$Order** (and Save to DB)
      - Set **HasShipmentDetails** = `true`
      - Set **ShippedTotalQuantity** = `$SumShippedQty`
      - Set **ShippedTotalSKU** = `$CountOfSKUs`
      - Set **ShipDate** = `$OrderHistory/ActualShipDate`
      - Set **ShipMethod** = `$OrderHistory/ShipVia`
      - Set **ShipToAddress** = `$ShipToAddress/Line1 + ', '+$ShipToAddress/City +', '+$ShipToAddress/Country`
      - Set **ShippedTotalPrice** = `$TotalShippedPrice`**
      9. **Retrieve related **Offer_Order** via Association from **$Order** (Result: **$Offer**)**
      10. **Update **$Offer** (and Save to DB)
      - Set **Offer_OrderStatus** = `$ShipCompleteStatus`
      - Set **ShippedQuantity** = `$SumShippedQty`**
      11. **Commit/Save **$ShipmentDetailList** to Database**
      12. **Commit/Save **$OfferItemList** to Database**
      13. **Commit/Save **$IMEIDetailList** to Database**
      14. 🔀 **DECISION:** `$Offer/ShippedQuantity= $Offer/OrderPackQuantity - $Offer/CanceledQuantity`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_ImeiData_UpdateSnowflake****
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. **Call Microflow **EcoATM_PWS.SUB_ImeiData_UpdateSnowflake****
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Error****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.