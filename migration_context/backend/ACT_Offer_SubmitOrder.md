# Microflow Detailed Specification: ACT_Offer_SubmitOrder

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)
- **$ENUM_OfferType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSSubmitOfferAndOrder'`**
2. **Retrieve related **BuyerOffer_BuyerCode** via Association from **$BuyerOffer** (Result: **$BuyerCode**)**
3. **Create Variable **$Description** = `'SalesRep -> Finalize Order - Submit buyer offer order [BuyerCode:'+$BuyerCode/Code+'] [OfferId:'+$BuyerOffer/OfferID+']'`**
4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
5. **Call Microflow **Custom_Logging.SUB_Log_Info****
6. **DB Retrieve **EcoATM_PWS.BuyerOfferItem** Filter: `[EcoATM_PWS.BuyerOfferItem_BuyerOffer=$BuyerOffer] [TotalPrice>0]` (Result: **$BuyerOfferItemWithPriceList**)**
7. 🔀 **DECISION:** `$BuyerOfferItemWithPriceList!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.SUB_BuyerOffer_CreateOffer** (Result: **$Offer**)**
      2. 🔀 **DECISION:** `$Offer != empty`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_BuyerOffer_RemoveRecords****
            2. **Call Microflow **EcoATM_PWS.SUB_Order_CreateFromOffer** (Result: **$Order**)**
            3. **Call Microflow **EcoATM_PWS.SUB_Order_PrepareContentAndSendToOracle** (Result: **$CreateOrderResponse**)**
            4. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$CurrentLoggedInUser**)**
            5. 🔀 **DECISION:** `$CreateOrderResponse!=empty`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$CreateOrderResponse/ReturnCode='00'`
                     ➔ **If [false]:**
                        1. **Update **$Order** (and Save to DB)
      - Set **OrderNumber** = `$CreateOrderResponse/OrderNumber`
      - Set **OracleOrderStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OrderLine** = `$CreateOrderResponse/OrderId`
      - Set **OracleHTTPCode** = `$CreateOrderResponse/HTTPCode`
      - Set **OracleJSONResponse** = `$CreateOrderResponse/JSONResponse`
      - Set **OrderDate** = `[%CurrentDateTime%]`
      - Set **IsSuccessful** = `$CreateOrderResponse/ReturnCode='00'`
      - Set **Offer_Order** = `$Offer`
      - Set **OrderCreatedBy_Account** = `$CurrentLoggedInUser`**
                        2. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus**)**
                        3. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus`**
                        4. **Call Microflow **EcoATM_PWS.SUB_SendPWSPendingOrderEmail****
                        5. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
                        6. **Call Microflow **EcoATM_PWSIntegration.SUB_PWSErrorCode_GetMessage** (Result: **$PWSResponseConfig**)**
                        7. **Close current page/popup**
                        8. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                        9. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage_1**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `if($PWSResponseConfig!=empty and $PWSResponseConfig/ByPassForUser=false) then $PWSResponseConfig/UserErrorCode + ' - ' + $PWSResponseConfig/UserErrorMessage else 'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                        10. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                        11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        12. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_PWS.SUB_CreateOrderResponse_ManageResult****
                        2. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
                        3. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
                        4. **Close current page/popup**
                        5. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                        6. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage**)
      - Set **Title** = `'Thank you for your order!'`
      - Set **Message** = `'Order Number: ' + $CreateOrderResponse/OrderNumber`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                        7. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                        8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        9. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$Order** (and Save to DB)
      - Set **Offer_Order** = `$Offer`
      - Set **OrderCreatedBy_Account** = `$CurrentLoggedInUser`**
                  2. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
                  3. **Close current page/popup**
                  4. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                  5. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                  6. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                  7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  8. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.