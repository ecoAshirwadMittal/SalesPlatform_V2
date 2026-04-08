# Microflow Detailed Specification: SUB_GenerateReservedQuantityForCaseLot

### 📥 Inputs (Parameters)
- **$CaseLot** (Type: EcoATM_PWSMDM.CaseLot)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_CaseLot=$CaseLot] [SalesOfferItemStatus= 'Accept' or SalesOfferItemStatus= 'Finalize' or (SalesOfferItemStatus='Counter' and BuyerCounterStatus='Accept')] [(EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer [OfferStatus= 'Sales_Review' or OfferStatus='Buyer_Acceptance' or OfferStatus= 'Pending_Order' ])or (EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus='Ordered' and not (OrderSynced)) ]` (Result: **$OfferItemList**)**
2. **AggregateList**
3. 🏁 **END:** Return `$TotalReservedQuantity`

**Final Result:** This process concludes by returning a [Decimal] value.