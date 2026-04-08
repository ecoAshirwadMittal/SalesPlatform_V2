# Microflow Detailed Specification: SUB_Offer_CreateOrder

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)
- **$OfferItemList** (Type: EcoATM_PWS.OfferItem)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'Create Offer based on the Offer [OfferID:'+$Offer/OfferID+']'`**
2. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
3. **Call Microflow **Custom_Logging.SUB_Log_Info****
4. **Create **EcoATM_PWS.Order** (Result: **$NewOrder**)
      - Set **Order_BuyerCode** = `$BuyerCode`
      - Set **Offer_Order** = `$Offer`
      - Set **HasShipmentDetails** = `not($FeatureFlagState)`**
5. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Update **$IteratorOfferItem**
      - Set **OfferItem_Order** = `$NewOrder`**
   └─ **End Loop**
6. **Commit/Save **$OfferItemList** to Database**
7. **Call Microflow **Custom_Logging.SUB_Log_Info****
8. 🏁 **END:** Return `$NewOrder`

**Final Result:** This process concludes by returning a [Object] value.