# Microflow Detailed Specification: SUB_NavigateToCounterOffers

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[EcoATM_PWS.Offer_BuyerCode=$BuyerCode] [OfferStatus='Buyer_Acceptance']` (Result: **$OfferList**)**
2. **AggregateList**
3. 🔀 **DECISION:** `$Count=0`
   ➔ **If [true]:**
      1. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **EcoATM_PWS.NAV_PWSCounterOffers****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.