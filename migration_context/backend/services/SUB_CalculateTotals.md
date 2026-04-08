# Microflow Detailed Specification: SUB_CalculateTotals

### 📥 Inputs (Parameters)
- **$OrderItemList** (Type: EcoATM_PWS.BuyerOfferItem)
- **$LastBuyerOffer** (Type: EcoATM_PWS.BuyerOffer)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BuyerOfferItem_RedList_Qty**)**
2. **AggregateList**
3. **AggregateList**
4. **AggregateList**
5. **Update **$LastBuyerOffer** (and Save to DB)
      - Set **OfferTotal** = `$SumTotalPrice`
      - Set **OfferSKUs** = `round($TotalSKUs)`
      - Set **OfferQuantity** = `round($TotalQuantity)`
      - Set **IsExceedingQty** = `length($BuyerOfferItem_RedList_Qty) > 0`**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.