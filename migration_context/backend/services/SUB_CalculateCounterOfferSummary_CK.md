# Microflow Detailed Specification: SUB_CalculateCounterOfferSummary_CK

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
3. **AggregateList**
4. **Create Variable **$CounterQty** = `0`**
5. **Create Variable **$CounterPriceTotal** = `0`**
6. **Create Variable **$SumDifference** = `0`**
7. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. 🔀 **DECISION:** `$IteratorOfferItem/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter`
   │    ➔ **If [true]:**
   │       1. **Update Variable **$CounterQty** = `$CounterQty+$IteratorOfferItem/CounterQuantity`**
   │       2. **Update Variable **$CounterPriceTotal** = `$CounterPriceTotal+($IteratorOfferItem/CounterPrice*$IteratorOfferItem/CounterQuantity)`**
   │       3. **Update Variable **$SumDifference** = `$SumDifference+($IteratorOfferItem/CounterPrice-$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/CurrentMinPrice)`**
   │    ➔ **If [false]:**
   │       1. **Update Variable **$CounterQty** = `$CounterQty+$IteratorOfferItem/OfferQuantity`**
   │       2. **Update Variable **$CounterPriceTotal** = `$CounterPriceTotal+($IteratorOfferItem/OfferPrice* $IteratorOfferItem/OfferQuantity)`**
   │       3. **Update Variable **$SumDifference** = `$SumDifference+($IteratorOfferItem/OfferPrice-$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/CurrentMinPrice)`**
   └─ **End Loop**
8. **Update **$Offer** (and Save to DB)
      - Set **CounterOfferTotalSKU** = `$SKUs_Counter`
      - Set **CounterOfferTotalQty** = `parseInteger(toString($CounterQty))`
      - Set **CounterOfferTotalPrice** = `parseInteger(toString($CounterPriceTotal))`
      - Set **CounterOfferAvgPrice** = `round($CounterPriceTotal div $CounterQty)`
      - Set **CounterOfferMinPercentageVariance** = `(($SumDifference)div $CounterPriceTotal)*100`
      - Set **UpdateDate** = `[%CurrentDateTime%]`**
9. **Call Microflow **Custom_Logging.SUB_Log_Info****
10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.