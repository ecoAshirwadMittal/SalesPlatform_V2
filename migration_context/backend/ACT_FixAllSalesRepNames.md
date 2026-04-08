# Microflow Detailed Specification: ACT_FixAllSalesRepNames

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSLoadInventoryData'`**
2. **Create Variable **$Description** = `'Updating All Sales Rep Admin'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[not(EcoATM_PWS.Offer_SalesRepresentative/EcoATM_BuyerManagement.SalesRepresentative)]` (Result: **$OfferList**)**
5. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
   │ 1. **Update **$IteratorOffer**
      - Set **Offer_SalesRepresentative** = `$IteratorOffer/EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_BuyerManagement.Buyer_SalesRepresentative`**
   └─ **End Loop**
6. **Commit/Save **$OfferList** to Database**
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.