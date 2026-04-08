# Microflow Detailed Specification: VAL_RMARequestFile

### 📥 Inputs (Parameters)
- **$RMARequest_ImportHelperList** (Type: EcoATM_RMA.RMARequest_ImportHelper)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$RMAFile** (Type: EcoATM_RMA.RMAFile)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$IsValidRMA** = `true`**
2. **CreateList**
3. **CreateList**
4. **DB Retrieve **EcoATM_RMA.RMAReasons** Filter: `[ ( IsActive = true() ) ]` (Result: **$RMAReasonsList**)**
5. **Create **EcoATM_RMA.InvalidRMAItem_UiHelper** (Result: **$NewInvalidRMAItem**)
      - Set **InvalidRMAItem_UiHelper_RMA** = `$RMA`**
6. **CreateList**
7. 🔄 **LOOP:** For each **$IteratorRMARequest_ImportHelper** in **$RMARequest_ImportHelperList**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorRMARequest_ImportHelper/IMEISerial_Number` (Result: **$RMAItem_Duplicate**)**
   │ 2. 🔀 **DECISION:** `$RMAItem_Duplicate = empty`
   │    ➔ **If [true]:**
   │       1. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[ ( EcoATM_PWS.OfferItem_BuyerCode = $BuyerCode and EcoATM_PWS.IMEIDetail_OfferItem/EcoATM_PWS.IMEIDetail/IMEINumber = $IteratorRMARequest_ImportHelper/IMEISerial_Number ) ]` (Result: **$OfferItem**)**
   │       2. 🔀 **DECISION:** `$OfferItem != empty`
   │          ➔ **If [true]:**
   │             1. **List Operation: **Find** on **$undefined** where `$IteratorRMARequest_ImportHelper/Return_Reason` (Result: **$RMAReasons**)**
   │             2. 🔀 **DECISION:** `$RMAReasons != empty`
   │                ➔ **If [true]:**
   │                   1. **Retrieve related **OfferItem_Order** via Association from **$OfferItem** (Result: **$Order**)**
   │                   2. **Retrieve related **OfferItem_Device** via Association from **$OfferItem** (Result: **$Device**)**
   │                   3. **Add **$$Device** to/from list **$DeviceList****
   │                   4. **Create **EcoATM_RMA.RMAItem** (Result: **$NewRMAItem**)
      - Set **RMAItem_RMA** = `$RMA`
      - Set **RMAItem_Device** = `$Device`
      - Set **RMAItem_Order** = `$Order`
      - Set **IMEI** = `$IteratorRMARequest_ImportHelper/IMEISerial_Number`
      - Set **ReturnReason** = `$IteratorRMARequest_ImportHelper/Return_Reason`
      - Set **ShipDate** = `$Order/ShipDate`
      - Set **OrderNumber** = `$Order/OrderNumber`
      - Set **SalePrice** = `$OfferItem/FinalOfferPrice`**
   │                   5. **Add **$$NewRMAItem** to/from list **$RMAItemList****
   │                ➔ **If [false]:**
   │                   1. **Create **EcoATM_RMA.InvalidIMEI_ExportHelper** (Result: **$InvalidReason**)
      - Set **IMEI** = `$IteratorRMARequest_ImportHelper/IMEISerial_Number`
      - Set **Reason** = `'Invalid Reason'`**
   │                   2. **Update **$NewInvalidRMAItem**
      - Set **InvalidReason** = `if $NewInvalidRMAItem/InvalidReasonCount >=4 then $NewInvalidRMAItem/InvalidReason else if $NewInvalidRMAItem/InvalidReason = empty then $IteratorRMARequest_ImportHelper/IMEISerial_Number else $NewInvalidRMAItem/InvalidReason + ' ' + $IteratorRMARequest_ImportHelper/IMEISerial_Number`
      - Set **InvalidReasonCount** = `$NewInvalidRMAItem/InvalidReasonCount + 1`
      - Set **InvalidRMAItem_UiHelper_InvalidIMEI_ExportHelper** = `$InvalidReason`**
   │                   3. **Add **$$InvalidReason** to/from list **$InvalidIMEI_ExportHelperList****
   │                   4. **Update Variable **$IsValidRMA** = `false`**
   │          ➔ **If [false]:**
   │             1. **Create **EcoATM_RMA.InvalidIMEI_ExportHelper** (Result: **$InvalidIMEI**)
      - Set **IMEI** = `$IteratorRMARequest_ImportHelper/IMEISerial_Number`
      - Set **Reason** = `'Invalid IMEI'`**
   │             2. **Update **$NewInvalidRMAItem**
      - Set **InvalidIMEI** = `if $NewInvalidRMAItem/InvalidIMEICount >=4 then $NewInvalidRMAItem/InvalidIMEI else if $NewInvalidRMAItem/InvalidIMEI = empty then $IteratorRMARequest_ImportHelper/IMEISerial_Number else $NewInvalidRMAItem/InvalidIMEI + ' ' + $IteratorRMARequest_ImportHelper/IMEISerial_Number`
      - Set **InvalidIMEICount** = `$NewInvalidRMAItem/InvalidIMEICount + 1`
      - Set **InvalidRMAItem_UiHelper_InvalidIMEI_ExportHelper** = `$InvalidIMEI`**
   │             3. **Add **$$InvalidIMEI** to/from list **$InvalidIMEI_ExportHelperList****
   │             4. **Update Variable **$IsValidRMA** = `false`**
   │    ➔ **If [false]:**
   │       1. **Create **EcoATM_RMA.InvalidIMEI_ExportHelper** (Result: **$DuplicateIMEI**)
      - Set **IMEI** = `$IteratorRMARequest_ImportHelper/IMEISerial_Number`
      - Set **Reason** = `'Duplicate IMEI'`**
   │       2. **Update **$NewInvalidRMAItem**
      - Set **DuplicateIMEI** = `if $NewInvalidRMAItem/DuplicateIMEICount >=4 then $NewInvalidRMAItem/DuplicateIMEI else if $NewInvalidRMAItem/DuplicateIMEI = empty then $IteratorRMARequest_ImportHelper/IMEISerial_Number else $NewInvalidRMAItem/DuplicateIMEI + ' ' + $IteratorRMARequest_ImportHelper/IMEISerial_Number`
      - Set **DuplicateIMEICount** = `$NewInvalidRMAItem/DuplicateIMEICount + 1`
      - Set **InvalidRMAItem_UiHelper_InvalidIMEI_ExportHelper** = `$DuplicateIMEI`**
   │       3. **Add **$$DuplicateIMEI** to/from list **$InvalidIMEI_ExportHelperList****
   │       4. **Update Variable **$IsValidRMA** = `false`**
   └─ **End Loop**
8. **Update **$RMAFile**
      - Set **IsValid** = `$IsValidRMA`**
9. 🔀 **DECISION:** `$IsValidRMA`
   ➔ **If [true]:**
      1. **AggregateList**
      2. **Update **$RMA**
      - Set **RequestSKUs** = `length($DeviceList)`
      - Set **RequestSalesTotal** = `$RequestSalesTotal`
      - Set **RequestQty** = `length($RMAItemList)`**
      3. 🏁 **END:** Return `$RMAItemList`
   ➔ **If [false]:**
      1. **Commit/Save **$NewInvalidRMAItem** to Database**
      2. **Commit/Save **$InvalidIMEI_ExportHelperList** to Database**
      3. 🏁 **END:** Return `$RMAItemList`

**Final Result:** This process concludes by returning a [List] value.