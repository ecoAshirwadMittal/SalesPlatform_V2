# Microflow Detailed Specification: SUB_CalculateRMAApprovedSummary

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$RMAItemList** (Type: EcoATM_RMA.RMAItem)

### ⚙️ Execution Flow (Logic Steps)
1. **AggregateList**
2. **Create Variable **$SalesTotal** = `0`**
3. **CreateList**
4. 🔄 **LOOP:** For each **$IteratorRMAItem** in **$RMAItemList**
   │ 1. **Update Variable **$SalesTotal** = `$SalesTotal+($IteratorRMAItem/SalePrice)`**
   │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_RMA.RMAItem_Device/EcoATM_PWSMDM.Device/SKU = $IteratorRMAItem/EcoATM_RMA.RMAItem_Device/EcoATM_PWSMDM.Device/SKU` (Result: **$RMAItemMatch**)**
   │ 3. 🔀 **DECISION:** `$RMAItemMatch = empty`
   │    ➔ **If [true]:**
   │       1. **Add **$$IteratorRMAItem** to/from list **$DistinctSKUs****
   │    ➔ **If [false]:**
   └─ **End Loop**
5. **AggregateList**
6. **Update **$RMA**
      - Set **ApprovedSKUs** = `$SKUs`
      - Set **ApprovedQty** = `$Qty`
      - Set **ApprovedSalesTotal** = `$SalesTotal`**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.