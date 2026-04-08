# Microflow Detailed Specification: ACT_BuyerOffer_SubmitOrder_2

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'BuyerOfferPrepareSubmitOrder'`**
2. **Create Variable **$Description** = `'prepare Order based on a BuyerOffer [BuyerOffer:'+$BuyerOffer/OfferID+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_PWS.BuyerOfferItem** Filter: `[EcoATM_PWS.BuyerOfferItem_BuyerOffer=$BuyerOffer] [TotalPrice>0]` (Result: **$BuyerOfferItemWithPriceList**)**
5. 🔀 **DECISION:** `$BuyerOfferItemWithPriceList!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **BuyerOffer_BuyerCode** via Association from **$BuyerOffer** (Result: **$BuyerCode**)**
      2. **Call Microflow **EcoATM_PWS.SUB_BuyerOffer_CreateOfferAndOrder** (Result: **$Offer**)**
      3. **Retrieve related **Offer_Order** via Association from **$Offer** (Result: **$Order**)**
      4. **Call Microflow **EcoATM_PWS.SUB_Offer_PrepareOraclePayload** (Result: **$JSONContent**)**
      5. **Call Microflow **EcoATM_PWS.SUB_Order_SendOrderToOracle** (Result: **$CreateOrderResponse**)**
      6. 🔀 **DECISION:** `$CreateOrderResponse!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$CreateOrderResponse/ReturnCode='00'`
               ➔ **If [true]:**
                  1. **Update **$Offer**
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Ordered`**
                  2. **Update **$Order** (and Save to DB)
      - Set **OrderNumber** = `$CreateOrderResponse/OrderNumber`
      - Set **OrderStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OrderLine** = `$CreateOrderResponse/OrderId`
      - Set **JSONContent** = `$JSONContent`**
                  3. **Call Microflow **EcoATM_PWS.SUB_BuyerOffer_RemoveRecords****
                  4. **Close current page/popup**
                  5. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                  6. **Create **EcoATM_PWS.UserMessage** (Result: **$SuccessUserMessage**)
      - Set **Title** = `'Your order has been submitted'`
      - Set **Message** = `'Order Number ' + $CreateOrderResponse/OrderNumber`
      - Set **CSSClass** = `'pws-file-upload-success'`**
                  7. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                  8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  9. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **EcoATM_PWSIntegration.SUB_PWSErrorCode_GetMessage** (Result: **$PWSResponseConfig**)**
                  2. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage_1**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `if($PWSResponseConfig!=empty and $PWSResponseConfig/ByPassForUser=false) then $PWSResponseConfig/UserErrorCode + ' - ' + $PWSResponseConfig/UserErrorMessage else 'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`**
                  3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  4. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                  5. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`**
            3. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
            4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.