# Microflow Detailed Specification: SUB_BuyerOfferItem_CalculateCaseLotSKUs

### 📥 Inputs (Parameters)
- **$BuyerOfferItemList_SPB** (Type: EcoATM_PWS.BuyerOfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. 🔄 **LOOP:** For each **$IteratorBuyerOfferItem** in **$BuyerOfferItemList_SPB**
   │ 1. **List Operation: **FilterByExpression** on **$undefined** where `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/SKU=$currentObject/SKU` (Result: **$SKUHelperList**)**
   │ 2. 🔀 **DECISION:** `$SKUHelperList=empty`
   │    ➔ **If [true]:**
   │       1. **Create **EcoATM_PWS.SKUHelper** (Result: **$NewSKUHelper**)
      - Set **SKU** = `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/SKU`**
   │       2. **Add **$$NewSKUHelper** to/from list **$UniqueSKUs****
   │    ➔ **If [false]:**
   └─ **End Loop**
3. **AggregateList**
4. **Delete**
5. 🏁 **END:** Return `$Count`

**Final Result:** This process concludes by returning a [Integer] value.