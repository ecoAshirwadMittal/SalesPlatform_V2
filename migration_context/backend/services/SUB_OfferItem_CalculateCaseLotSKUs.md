# Microflow Detailed Specification: SUB_OfferItem_CalculateCaseLotSKUs

### 📥 Inputs (Parameters)
- **$OfferItemList** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/EcoATM_PWSMDM.CaseLot_Device/EcoATM_PWSMDM.Device/ItemType='SPB'` (Result: **$OfferItemList_SPB**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList_SPB**
   │ 1. **List Operation: **FilterByExpression** on **$undefined** where `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU=$currentObject/SKU` (Result: **$SKUHelperList**)**
   │ 2. 🔀 **DECISION:** `$SKUHelperList=empty`
   │    ➔ **If [true]:**
   │       1. **Create **EcoATM_PWS.SKUHelper** (Result: **$NewSKUHelper**)
      - Set **SKU** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU`**
   │       2. **Add **$$NewSKUHelper** to/from list **$UniqueSKUs****
   │    ➔ **If [false]:**
   └─ **End Loop**
4. **AggregateList**
5. **Delete**
6. 🏁 **END:** Return `$Count`

**Final Result:** This process concludes by returning a [Integer] value.