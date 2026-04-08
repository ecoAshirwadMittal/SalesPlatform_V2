# Microflow Detailed Specification: ACT_Offer_DownloadAJSONFile

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **EcoATM_PWS.SUB_Offer_ConvertToJSON** (Result: **$JSONContent**)**
3. **Create **EcoATM_PWS.ManageFileDocument** (Result: **$NewFileUploadProcessHelper**)
      - Set **Name** = `'Offer_'+$Offer/OfferID+'.json'`**
4. **JavaCallAction**
5. **DownloadFile**
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.