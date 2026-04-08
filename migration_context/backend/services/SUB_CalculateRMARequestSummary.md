# Microflow Detailed Specification: SUB_CalculateRMARequestSummary

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
6. **DB Retrieve **EcoATM_RMA.RMAStatus** Filter: `[IsDefaultStatus]` (Result: **$RMAStatus_Default**)**
7. **Update **$RMA**
      - Set **RequestSKUs** = `$SKUs`
      - Set **RequestQty** = `$Qty`
      - Set **RequestSalesTotal** = `$SalesTotal`
      - Set **SystemStatus** = `$RMAStatus_Default/SystemStatus`
      - Set **RMA_RMAStatus** = `$RMAStatus_Default`**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.