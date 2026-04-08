# Microflow Detailed Specification: SUB_BuyerOffer_CreateOfferAndOrder

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)
- **$BuyerOfferItemList** (Type: EcoATM_PWS.BuyerOfferItem)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'Create Offer based on the BuyerOffer [BuyerOfferID:'+$BuyerOffer/OfferID+']'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Create **EcoATM_PWS.Order** (Result: **$NewOrder**)
      - Set **Order_BuyerCode** = `$BuyerCode`**
4. **Create **EcoATM_PWS.Offer** (Result: **$NewOffer**)
      - Set **Offer_BuyerCode** = `$BuyerOffer/EcoATM_PWS.BuyerOffer_BuyerCode`
      - Set **Offer_Order** = `$NewOrder`**
5. **CreateList**
6. 🔄 **LOOP:** For each **$IteratorBuyerOfferItem** in **$BuyerOfferItemList**
   │ 1. **Create **EcoATM_PWS.OfferItem** (Result: **$NewOfferItem**)
      - Set **OfferQuantity** = `$IteratorBuyerOfferItem/Quantity`
      - Set **OfferPrice** = `$IteratorBuyerOfferItem/OfferPrice`
      - Set **OfferTotalPrice** = `$IteratorBuyerOfferItem/TotalPrice`
      - Set **SalesOfferItemStatus** = `EcoATM_PWS.ENUM_OfferItemStatus.Accept`
      - Set **OfferItem_Offer** = `$NewOffer`
      - Set **OfferItem_Device** = `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device`
      - Set **OfferItem_BuyerCode** = `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_BuyerCode`
      - Set **OfferItem_Order** = `$NewOrder`**
   │ 2. **Add **$$NewOfferItem
** to/from list **$OfferItemList****
   └─ **End Loop**
7. **AggregateList**
8. **AggregateList**
9. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
10. **Update **$NewOffer** (and Save to DB)
      - Set **OfferTotalQuantity** = `$SumTotalQuantity`
      - Set **OfferTotalPrice** = `$SumTotalPrice`
      - Set **OfferSubmissionDate** = `[%CurrentDateTime%]`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **OfferSubmittedBy_Account** = `$Account`**
11. **Commit/Save **$OfferItemList** to Database**
12. **Call Microflow **Custom_Logging.SUB_Log_Info****
13. 🏁 **END:** Return `$NewOffer`

**Final Result:** This process concludes by returning a [Object] value.