# Microflow Detailed Specification: SUB_Offer_RecalculateOffer

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
2. **Call Microflow **EcoATM_PWS.VAL_Offer_Finalize** (Result: **$IsFinalizeValid**)**
3. 🔀 **DECISION:** `$IsFinalizeValid`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.VAL_Offer_Counter** (Result: **$isValid**)**
      2. 🔀 **DECISION:** `$isValid`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_CalculateCounterOfferSummary****
            2. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Update **$Offer** (and Save to DB)
      - Set **CounterOfferTotalSKU** = `0`
      - Set **CounterOfferTotalQty** = `0`
      - Set **CounterOfferTotalPrice** = `0`
      - Set **CounterOfferAvgPrice** = `0`
      - Set **CounterOfferMinPercentageVariance** = `0`
      - Set **isValidOffer** = `false`**
            2. **Call Microflow **Custom_Logging.SUB_Log_Info****
            3. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **Update **$Offer** (and Save to DB)
      - Set **CounterOfferTotalSKU** = `0`
      - Set **CounterOfferTotalQty** = `0`
      - Set **CounterOfferTotalPrice** = `0`
      - Set **CounterOfferAvgPrice** = `0`
      - Set **CounterOfferMinPercentageVariance** = `0`
      - Set **isValidOffer** = `false`**
      2. **Call Microflow **Custom_Logging.SUB_Log_Info****
      3. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.