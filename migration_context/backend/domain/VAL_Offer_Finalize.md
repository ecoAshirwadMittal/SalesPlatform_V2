# Microflow Detailed Specification: VAL_Offer_Finalize

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)
- **$OfferItemList** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Finalize` (Result: **$FinalizeOfferItemList**)**
3. 🔀 **DECISION:** `$FinalizeOfferItemList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter or $currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Accept or $currentObject/SalesOfferItemStatus=empty` (Result: **$EmptyOrCounterOrAccepteOfferItemList**)**
      2. 🔀 **DECISION:** `$EmptyOrCounterOrAccepteOfferItemList=empty`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `false`
         ➔ **If [true]:**
            1. **Create Variable **$isValid** = `true`**
            2. 🔄 **LOOP:** For each **$IteratorFinalizeOfferItem** in **$FinalizeOfferItemList**
               │ 1. 🔀 **DECISION:** `$IteratorFinalizeOfferItem/CounterPrice!=empty and $IteratorFinalizeOfferItem/CounterPrice>0 and $IteratorFinalizeOfferItem/CounterQuantity!=empty and $IteratorFinalizeOfferItem/CounterQuantity>0`
               │    ➔ **If [false]:**
               │       1. **Update Variable **$isValid** = `false`**
               │    ➔ **If [true]:**
               └─ **End Loop**
            3. **Commit/Save **$OfferItemList** to Database**
            4. **Call Microflow **Custom_Logging.SUB_Log_Info****
            5. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.