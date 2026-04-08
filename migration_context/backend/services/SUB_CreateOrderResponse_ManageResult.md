# Microflow Detailed Specification: SUB_CreateOrderResponse_ManageResult

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)
- **$Order** (Type: EcoATM_PWS.Order)
- **$CreateOrderResponse** (Type: EcoATM_PWSIntegration.OracleResponse)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$CreateOrderResponse!=empty`
   ➔ **If [false]:**
      1. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus_1**)**
      2. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **FinalOfferSubmittedOn** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus_1`**
      3. **Call Microflow **EcoATM_PWS.SUB_SendPWSPendingOrderEmail****
      4. **Commit/Save **$Order** to Database**
      5. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus**)**
      2. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `if($CreateOrderResponse/ReturnCode='00') then EcoATM_PWS.ENUM_PWSOrderStatus.Ordered else EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **FinalOfferSubmittedOn** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus`**
      3. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$CurrentLoggedInUser**)**
      4. **Update **$Order** (and Save to DB)
      - Set **OrderNumber** = `$CreateOrderResponse/OrderNumber`
      - Set **OracleOrderStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OrderLine** = `$CreateOrderResponse/OrderId`
      - Set **OracleHTTPCode** = `$CreateOrderResponse/HTTPCode`
      - Set **OracleJSONResponse** = `$CreateOrderResponse/JSONResponse`
      - Set **OrderDate** = `[%CurrentDateTime%]`
      - Set **IsSuccessful** = `$CreateOrderResponse/ReturnCode='00'`
      - Set **Offer_Order** = `$Offer`
      - Set **OrderCreatedBy_Account** = `$CurrentLoggedInUser`**
      5. **Call Microflow **EcoATM_PWS.SUB_CheckIfQtyAdjusted** (Result: **$QtyAdjustedFound**)**
      6. 🔀 **DECISION:** `$QtyAdjustedFound`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_SendPWSAdjustedQuantityOrderConfirmationEmail****
            2. 🔀 **DECISION:** `$Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_PWS.SUB_SendPWSPendingOrderEmail****
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **EcoATM_PWS.SUB_SendPWSOrderConfirmationEmail****
            2. 🔀 **DECISION:** `$Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_PWS.SUB_SendPWSPendingOrderEmail****
                  2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.