# Microflow Detailed Specification: VAL_Offer_Counter

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)
- **$OfferItemList** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'Offer Validation'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Create Variable **$isValid** = `true`**
4. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter` (Result: **$CounterOfferItemList**)**
5. 🔀 **DECISION:** `$CounterOfferItemList!=empty`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorCounterOfferItem** in **$CounterOfferItemList**
         │ 1. 🔀 **DECISION:** `$IteratorCounterOfferItem/CounterPrice!=empty and $IteratorCounterOfferItem/CounterPrice>0 and $IteratorCounterOfferItem/CounterQuantity!=empty and $IteratorCounterOfferItem/CounterQuantity>0 and $IteratorCounterOfferItem/ValidQty`
         │    ➔ **If [false]:**
         │       1. **Update Variable **$isValid** = `false`**
         │    ➔ **If [true]:**
         └─ **End Loop**
      2. 🔄 **LOOP:** For each **$IteratorCounterOfferItem_1** in **$CounterOfferItemList**
         │ 1. 🔀 **DECISION:** `$IteratorCounterOfferItem_1/ValidQty`
         │    ➔ **If [false]:**
         │       1. **Update **$Offer**
      - Set **AllSKUsWithValidOffer** = `$IteratorCounterOfferItem_1/ValidQty`**
         │    ➔ **If [true]:**
         │       1. **Update **$Offer**
      - Set **AllSKUsWithValidOffer** = `$IteratorCounterOfferItem_1/ValidQty`**
         └─ **End Loop**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.