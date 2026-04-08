# Microflow Detailed Specification: SUB_OfferItem_DetermineQuantityCSSStyle

### 📥 Inputs (Parameters)
- **$OfferItem** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$OfferItem/SalesOfferItemStatus`
   ➔ **If [Accept]:**
      1. **Retrieve related **OfferItem_Device** via Association from **$OfferItem** (Result: **$Device**)**
      2. 🏁 **END:** Return `if($Device/AvailableQty!=empty and $OfferItem/CounterQuantity!=empty) then if($Device/AvailableQty<$OfferItem/OfferQuantity) then 'offer-price-orange' else 'offer-price-green' else 'pws-datagrid-background-color'`
   ➔ **If [(empty)]:**
      1. 🏁 **END:** Return `'pws-datagrid-background-color'`
   ➔ **If [Counter]:**
      1. **Retrieve related **OfferItem_Device** via Association from **$OfferItem** (Result: **$Device_1**)**
      2. 🏁 **END:** Return `if($Device_1/AvailableQty!=empty and $OfferItem/CounterQuantity!=empty) then if($Device_1/AvailableQty<$OfferItem/CounterQuantity) then 'offer-price-orange' else 'offer-price-green' else 'pws-datagrid-background-color'`
   ➔ **If [Finalize]:**
      1. **Retrieve related **OfferItem_Device** via Association from **$OfferItem** (Result: **$Device_1**)**
      2. 🏁 **END:** Return `if($Device_1/AvailableQty!=empty and $OfferItem/CounterQuantity!=empty) then if($Device_1/AvailableQty<$OfferItem/CounterQuantity) then 'offer-price-orange' else 'offer-price-green' else 'pws-datagrid-background-color'`
   ➔ **If [Decline]:**
      1. 🏁 **END:** Return `'pws-datagrid-background-color'`

**Final Result:** This process concludes by returning a [String] value.