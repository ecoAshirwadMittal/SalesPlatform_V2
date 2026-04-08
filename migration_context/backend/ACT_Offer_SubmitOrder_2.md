# Microflow Detailed Specification: ACT_Offer_SubmitOrder_2

### 📥 Inputs (Parameters)
- **$FinalOffer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer=$FinalOffer] [SalesOfferItemStatus='Accept' or (SalesOfferItemStatus='Counter' and BuyerCounterStatus='Accept')]` (Result: **$AcceptedOfferItemList**)**
3. 🔀 **DECISION:** `$AcceptedOfferItemList!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **Offer_BuyerCode** via Association from **$FinalOffer** (Result: **$BuyerCode**)**
      2. **Call Microflow **EcoATM_PWS.SUB_Offer_CreateOrder** (Result: **$Order**)**
      3. **Call Microflow **EcoATM_PWS.SUB_Offer_PrepareOraclePayload** (Result: **$JSONContent**)**
      4. **Call Microflow **EcoATM_PWS.SUB_Order_SendOrderToOracle** (Result: **$CreateOrderResponse**)**
      5. 🔀 **DECISION:** `$CreateOrderResponse!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$CreateOrderResponse/ReturnCode='00'`
               ➔ **If [true]:**
                  1. **Update **$FinalOffer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Ordered`**
                  2. **Update **$Order** (and Save to DB)
      - Set **OrderNumber** = `$CreateOrderResponse/OrderNumber`
      - Set **OrderStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OrderLine** = `$CreateOrderResponse/OrderId`
      - Set **JSONContent** = `$JSONContent`**
                  3. **Close current page/popup**
                  4. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                  5. **Create **EcoATM_PWS.UserMessage** (Result: **$SuccessUserMessage**)
      - Set **Title** = `'Your order has been submitted'`
      - Set **Message** = `'Order Number ' + $CreateOrderResponse/OrderNumber`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                  6. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                  7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  8. 🏁 **END:** Return `$SuccessUserMessage`
               ➔ **If [false]:**
                  1. **Call Microflow **EcoATM_PWSIntegration.SUB_PWSErrorCode_GetMessage** (Result: **$PWSResponseConfig**)**
                  2. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage_1**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `if($PWSResponseConfig!=empty and $PWSResponseConfig/ByPassForUser=false) then $PWSResponseConfig/UserErrorCode + ' - ' + $PWSResponseConfig/UserErrorMessage else 'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                  3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  4. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                  5. 🏁 **END:** Return `$NewUserMessage_1`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
            3. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
            4. 🏁 **END:** Return `$NewUserMessage`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.UserMessage** (Result: **$NothingToOrderUserMessage**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$NothingToOrderUserMessage`

**Final Result:** This process concludes by returning a [Object] value.