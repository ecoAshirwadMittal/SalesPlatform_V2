# Microflow Detailed Specification: ACT_Buyer_ChooseUploadOfferExcelFile

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'UploadExcelOffer - Popup'`**
2. **Create Variable **$Description** = `'Excel file for importing BuyerOffer [BuyerCode:'+$BuyerCode/Code+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create **EcoATM_PWS.ManageFileDocument** (Result: **$OfferExcelImporUpdateFile**)
      - Set **Message** = `empty`
      - Set **ProcessPercentage** = `0`**
5. **Maps to Page: **EcoATM_PWS.BuyerOffer_Step1_SelectExcelFile****
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.