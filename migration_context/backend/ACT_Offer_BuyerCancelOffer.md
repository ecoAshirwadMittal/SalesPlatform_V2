# Microflow Detailed Specification: ACT_Offer_BuyerCancelOffer

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🔀 **DECISION:** `$ObjectInfo_1/IsCurrentUserAllowed`
   ➔ **If [false]:**
      1. **Maps to Page: **EcoATM_PWS.PWS_OfferAndCounterOffer_Review_Buyer****
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
      2. **Create Variable **$TimerName** = `'CancelOffer'`**
      3. **Create Variable **$Description** = `'Buyer Cancelled the counter offer [OfferId:'+$Offer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
      4. **Call Microflow **AuctionUI.ACT_GetCurrentUser** (Result: **$EcoATMDirectUser**)**
      5. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      6. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus**)**
      7. **Update **$Offer** (and Save to DB)
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Declined`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **FinalOfferTotalSKU** = `0`
      - Set **FinalOfferTotalQty** = `0`
      - Set **FinalOfferTotalPrice** = `0`
      - Set **BuyerOfferCancelled** = `true`
      - Set **OfferCancelledOn** = `[%CurrentDateTime%]`
      - Set **Offer_CancelledBy** = `$EcoATMDirectUser`
      - Set **Offer_OrderStatus** = `$OrderStatus`**
      8. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
      9. **Call Microflow **EcoATM_PWS.SUB_Offer_UpdateSnowflake****
      10. **Close current page/popup**
      11. **Maps to Page: **EcoATM_PWS.PWSBuyerCounterOffer_Overview****
      12. **Create **EcoATM_PWS.UserMessage** (Result: **$AcceptedUserMessage**)
      - Set **Title** = `'Offer response submitted'`
      - Set **Message** = `'You will receive details about your order shortly.'`
      - Set **CSSClass** = `'pws-file-upload-success'`
      - Set **IsSuccess** = `true`**
      13. **Maps to Page: **EcoATM_PWS.PWS_Message_View****
      14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.