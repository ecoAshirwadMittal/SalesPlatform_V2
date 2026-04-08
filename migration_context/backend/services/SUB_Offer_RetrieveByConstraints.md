# Microflow Detailed Specification: SUB_Offer_RetrieveByConstraints

### 📥 Inputs (Parameters)
- **$ChangeOfferStatusHelper** (Type: EcoATM_PWS.ChangeOfferStatusHelper)
- **$OrderStatus** (Type: EcoATM_PWS.OrderStatus)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/AllPeriod`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus=$ChangeOfferStatusHelper/FromOfferStatus] [EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus!=$OrderStatus]` (Result: **$AllPeriodOfferList**)**
      2. 🏁 **END:** Return `$AllPeriodOfferList`
   ➔ **If [false]:**
      1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus=$ChangeOfferStatusHelper/FromOfferStatus] [EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus!=$OrderStatus] [EcoATM_PWS.Offer_Order/EcoATM_PWS.Order[OrderDate>$ChangeOfferStatusHelper/StartingDate and OrderDate<$ChangeOfferStatusHelper/EndingDate]]` (Result: **$WiithPeriodOfferList**)**
      2. 🏁 **END:** Return `$WiithPeriodOfferList`

**Final Result:** This process concludes by returning a [List] value.