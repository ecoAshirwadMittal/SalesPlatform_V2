# Microflow Detailed Specification: ACT_Offer_CompleteReview

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **Administration.Account** Filter: `[id=$currentUser]` (Result: **$CurrentLoggedInUser**)**
      2. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
      3. **Create Variable **$TimerName** = `'CompleteOfferReview'`**
      4. **Create Variable **$Description** = `'Complete review for offer [OfferId:'+$Offer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
      5. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      6. **Call Microflow **AuctionUI.ACT_GetCurrentUser** (Result: **$EcoATMDirectUser**)**
      7. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
      8. **Call Microflow **EcoATM_PWS.SUB_CheckAllDeclined** (Result: **$AllDeclined**)**
      9. 🔀 **DECISION:** `$AllDeclined`
         ➔ **If [false]:**
            1. **Call Microflow **EcoATM_PWS.VAL_Offer_Finalize** (Result: **$IsFinalizeValid**)**
            2. 🔀 **DECISION:** `$IsFinalizeValid`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_PWS.VAL_Offer_Counter** (Result: **$isValid**)**
                  2. 🔀 **DECISION:** `$isValid`
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_PWS.VAL_Offer_hasRespectedAvailableQuantities** (Result: **$hasRespectedAvailableQuantities**)**
                        2. 🔀 **DECISION:** `$hasRespectedAvailableQuantities`
                           ➔ **If [true]:**
                              1. **List Operation: **Find** on **$undefined** where `EcoATM_PWS.ENUM_OfferItemStatus.Counter` (Result: **$CounterOfferItemFound**)**
                              2. 🔀 **DECISION:** `$CounterOfferItemFound =empty`
                                 ➔ **If [true]:**
                                    1. **Call Microflow **EcoATM_PWS.SUB_SetFinalValuesToAcceptedItems****
                                    2. **Call Microflow **EcoATM_PWS.SUB_Order_CreateFromOffer** (Result: **$Order**)**
                                    3. 🔀 **DECISION:** `$Order!=empty`
                                       ➔ **If [true]:**
                                          1. **Call Microflow **EcoATM_PWS.SUB_Order_PrepareContentAndSendToOracle** (Result: **$CreateOrderResponse**)**
                                          2. 🔀 **DECISION:** `$CreateOrderResponse!=empty`
                                             ➔ **If [true]:**
                                                1. 🔀 **DECISION:** `$CreateOrderResponse/ReturnCode='00'`
                                                   ➔ **If [true]:**
                                                      1. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus**)**
                                                      2. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Ordered`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus`**
                                                      3. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
                                                      4. **Update **$Order** (and Save to DB)
      - Set **OrderNumber** = `$CreateOrderResponse/OrderNumber`
      - Set **OracleOrderStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OrderLine** = `$CreateOrderResponse/OrderId`
      - Set **OrderDate** = `[%CurrentDateTime%]`
      - Set **JSONContent** = `$CreateOrderResponse/JSONResponse`
      - Set **OracleHTTPCode** = `$CreateOrderResponse/HTTPCode`
      - Set **IsSuccessful** = `true`
      - Set **Offer_Order** = `$Offer`
      - Set **OrderCreatedBy_Account** = `$CurrentLoggedInUser`**
                                                      5. **Call Microflow **EcoATM_PWS.SUB_CheckIfQtyAdjusted** (Result: **$QtyAdjustedFound**)**
                                                      6. 🔀 **DECISION:** `$QtyAdjustedFound`
                                                         ➔ **If [true]:**
                                                            1. **Call Microflow **EcoATM_PWS.SUB_SendPWSAdjustedQuantityOrderConfirmationEmail****
                                                            2. **Close current page/popup**
                                                            3. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                                                            4. **Create **EcoATM_PWS.UserMessage** (Result: **$SuccessUserMessage**)
      - Set **Title** = `'Your order has been submitted'`
      - Set **Message** = `'Order Number ' + $CreateOrderResponse/OrderNumber`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                                                            5. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                                                            6. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
                                                            7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                            8. 🏁 **END:** Return empty
                                                         ➔ **If [false]:**
                                                            1. **Call Microflow **EcoATM_PWS.SUB_SendPWSOrderConfirmationEmail****
                                                            2. **Close current page/popup**
                                                            3. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                                                            4. **Create **EcoATM_PWS.UserMessage** (Result: **$SuccessUserMessage**)
      - Set **Title** = `'Your order has been submitted'`
      - Set **Message** = `'Order Number ' + $CreateOrderResponse/OrderNumber`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                                                            5. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                                                            6. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
                                                            7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                            8. 🏁 **END:** Return empty
                                                   ➔ **If [false]:**
                                                      1. **Update **$Order** (and Save to DB)
      - Set **OrderNumber** = `$CreateOrderResponse/OrderNumber`
      - Set **OracleOrderStatus** = `$CreateOrderResponse/ReturnMessage`
      - Set **OrderLine** = `$CreateOrderResponse/OrderId`
      - Set **OrderDate** = `[%CurrentDateTime%]`
      - Set **JSONContent** = `$CreateOrderResponse/JSONResponse`
      - Set **OracleHTTPCode** = `$CreateOrderResponse/HTTPCode`
      - Set **IsSuccessful** = `false`
      - Set **Offer_Order** = `$Offer`
      - Set **OrderCreatedBy_Account** = `$CurrentLoggedInUser`**
                                                      2. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus_PO**)**
                                                      3. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus_PO`**
                                                      4. **Call Microflow **EcoATM_PWS.SUB_SendPWSPendingOrderEmail****
                                                      5. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
                                                      6. **Call Microflow **EcoATM_PWSIntegration.SUB_PWSErrorCode_GetMessage** (Result: **$PWSResponseConfig**)**
                                                      7. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage_1**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `if($PWSResponseConfig!=empty and $PWSResponseConfig/ByPassForUser=false) then $PWSResponseConfig/UserErrorCode + ' - ' + $PWSResponseConfig/UserErrorMessage else 'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                                                      8. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                                                      9. **Call Microflow **EcoATM_PWS.SUB_ShowPWSOffersPage****
                                                      10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                      11. 🏁 **END:** Return `empty`
                                             ➔ **If [false]:**
                                                1. **Update **$Order** (and Save to DB)
      - Set **OrderDate** = `[%CurrentDateTime%]`
      - Set **IsSuccessful** = `false`
      - Set **Offer_Order** = `$Offer`
      - Set **OrderCreatedBy_Account** = `$CurrentLoggedInUser`**
                                                2. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage**)
      - Set **Title** = `'Submit order failed'`
      - Set **Message** = `'Order is not placed. Please try Re-Submitting the Order'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                                                3. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                                                4. **Call Microflow **EcoATM_PWS.SUB_ShowPWSOffersPage****
                                                5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                6. 🏁 **END:** Return `empty`
                                       ➔ **If [false]:**
                                          1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          2. 🏁 **END:** Return `empty`
                                 ➔ **If [false]:**
                                    1. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus_BA**)**
                                    2. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Buyer_Acceptance`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **SalesReviewCompletedOn** = `[%CurrentDateTime%]`
      - Set **SalesReviewCompletedBy_Account** = `$CurrentLoggedInUser`
      - Set **OfferBeyondSLA** = `false`
      - Set **Offer_OrderStatus** = `$OrderStatus_BA`**
                                    3. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
                                    4. **Call Microflow **EcoATM_PWS.SUB_SendPWSCounterOfferEmail****
                                    5. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
                                    6. **Call Microflow **EcoATM_PWS.SUB_ShowPWSOffersPage****
                                    7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                    8. 🏁 **END:** Return empty
                           ➔ **If [false]:**
                              1. **Create **EcoATM_PWS.UserMessage** (Result: **$QuantityMessageError**)
      - Set **Title** = `'Complete Review'`
      - Set **Message** = `'Offered or Countered Qty is over the Available Qty for some SKUs. Please counter the Qty before completing the review'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                              2. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                              3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                              4. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Create **EcoATM_PWS.UserMessage** (Result: **$OfferCounterMessageError**)
      - Set **Title** = `'Complete Review'`
      - Set **Message** = `'All countered SKUs must be valid before completing review'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                        2. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                        3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Create **EcoATM_PWS.UserMessage** (Result: **$FinalizeMessageError**)
      - Set **Title** = `'Complete Review'`
      - Set **Message** = `'If choose finalize, all SKUs must also be set to either finalize with counter price and qty or decline.'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
                  2. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                  3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  4. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus_Declined**)**
            2. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Declined`
      - Set **SalesReviewCompletedOn** = `[%CurrentDateTime%]`
      - Set **OfferBeyondSLA** = `false`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **SellerOfferCancelled** = `true`
      - Set **OfferCancelledOn** = `[%CurrentDateTime%]`
      - Set **SalesReviewCompletedBy_Account** = `$CurrentLoggedInUser`
      - Set **Offer_CancelledBy** = `$EcoATMDirectUser`
      - Set **Offer_OrderStatus** = `$OrderStatus_Declined`**
            3. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
            4. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
            5. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage_Declined**)
      - Set **Title** = `'Offer Declined'`
      - Set **Message** = `'Offer has been declined since all offer items are declined'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
            6. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
            7. **Call Microflow **EcoATM_PWS.SUB_ShowPWSOffersPage****
            8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.