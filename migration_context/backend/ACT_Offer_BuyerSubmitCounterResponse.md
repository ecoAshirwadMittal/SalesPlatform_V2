# Microflow Detailed Specification: ACT_Offer_BuyerSubmitCounterResponse

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ObjectInfo/IsCurrentUserAllowed`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review_Buyer****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
      2. **Create Variable **$TimerName** = `'BuyerCounterResponse'`**
      3. **Create Variable **$Description** = `'counter offer [OfferId:'+$Offer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
      4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      5. **Call Microflow **EcoATM_PWS.VAL_Offer_IsCounterOfferReadyForSubmit** (Result: **$IsValid**)**
      6. 🔀 **DECISION:** `$IsValid`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_Offer_DefineFinalOfferStatus** (Result: **$FinalOfferStatus**)**
            2. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
            3. **Update **$Offer**
      - Set **CounterResponseSubmittedOn** = `[%CurrentDateTime%]`
      - Set **CounterOfferSubmittedBy_Account** = `$Account`**
            4. 🔀 **DECISION:** `$FinalOfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Ordered`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_PWS.SUB_Order_CreateFromOffer** (Result: **$Order**)**
                  2. **Call Microflow **EcoATM_PWS.SUB_Order_PrepareContentAndSendToOracle** (Result: **$CreateOrderResponse**)**
                  3. **Call Microflow **EcoATM_PWS.SUB_CreateOrderResponse_ManageResult****
                  4. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
                  5. **Close current page/popup**
                  6. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                  7. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage**)
      - Set **Title** = `'Offer response submitted'`
      - Set **Message** = `'You will receive details about your order shortly.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                  8. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
                  9. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                  10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  11. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus**)**
                  2. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `$FinalOfferStatus`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus`**
                  3. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
                  4. **Close current page/popup**
                  5. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
                  6. **Create **EcoATM_PWS.UserMessage** (Result: **$UserMessage**)
      - Set **Title** = `'Offer response submitted'`
      - Set **Message** = `'You will receive details about your order shortly.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
                  7. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
                  8. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
                  9. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  10. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. **Create **EcoATM_PWS.UserMessage** (Result: **$NewUserMessage**)
      - Set **Title** = `'Submit Response'`
      - Set **Message** = `'All countered SKUs must be either Accepted or Rejected before submitting'`
      - Set **CSSClass** = `'pws-file-upload-error'`
      - Set **IsSuccess** = `false`**
            3. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
            4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.