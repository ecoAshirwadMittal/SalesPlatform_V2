# Microflow Detailed Specification: ACT_Offer_SubmitOffer

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **BuyerOffer_BuyerCode** via Association from **$BuyerOffer** (Result: **$BuyerCode**)**
2. **Create Variable **$TimerName** = `'PWSSubmitOffer'`**
3. **Create Variable **$Description** = `'Submit buyer''s offer based on a BuyerOffer [BuyerOfferId:'+$BuyerOffer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
4. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
5. **DB Retrieve **EcoATM_PWS.BuyerOfferItem** Filter: `[EcoATM_PWS.BuyerOfferItem_BuyerOffer=$BuyerOffer] [TotalPrice>0]` (Result: **$BuyerOfferItemWithPriceList**)**
6. 🔀 **DECISION:** `$BuyerOfferItemWithPriceList!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.SUB_BuyerOffer_CreateOffer** (Result: **$Offer**)**
      2. 🔀 **DECISION:** `$Offer != empty`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus**)**
            2. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus`**
            3. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
            4. **Call Microflow **EcoATM_PWS.SUB_BuyerOffer_RemoveRecords****
            5. **Call Microflow **EcoATM_PWS.SUB_SendPWSOfferConfirmationEmail****
            6. **Close current page/popup**
            7. **Maps to Page: **EcoATM_PWS.PWSOrder_PE****
            8. **Create **EcoATM_PWS.UserMessage** (Result: **$SuccessUserMessage**)
      - Set **Title** = `'Offer submitted'`
      - Set **Message** = `'Your offer has been submitted offer number: ' + $Offer/OfferID`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
            9. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
            10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            11. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.