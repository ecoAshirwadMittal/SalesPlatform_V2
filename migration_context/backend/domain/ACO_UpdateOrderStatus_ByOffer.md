# Microflow Detailed Specification: ACO_UpdateOrderStatus_ByOffer

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'Offer Commit Event Handler'`**
2. **Create Variable **$Description** = `'Offer Commit Event Handler. [Offer buyer code:'+$Offer/EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code+'] [OfferId:'+$Offer/OfferID+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. 🔀 **DECISION:** `$Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Ordered or $Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWS.OrderStatus** Filter: `[ ( SystemStatus = 'Ordered' ) ]` (Result: **$OrderStatus**)**
      2. **Retrieve related **Offer_Order** via Association from **$Offer** (Result: **$Order**)**
      3. **Update **$Order**
      - Set **Order_OrderStatus** = `$OrderStatus`**
      4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      5. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.