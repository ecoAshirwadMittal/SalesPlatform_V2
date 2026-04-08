# Microflow Detailed Specification: SUB_Buyer_UploadExcelFile_2

### 📥 Inputs (Parameters)
- **$OfferExcelImportDocument** (Type: EcoATM_PWS.ManageFileDocument)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.VAL_OfferBuyerExcelFile_IsValid** (Result: **$IsValid**)**
2. 🔀 **DECISION:** `$IsValid`
   ➔ **If [true]:**
      1. **ImportExcelData**
      2. **Retrieve related **ManageFileDocument_Offer** via Association from **$OfferExcelImportDocument** (Result: **$Offer**)**
      3. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
      4. **Call Microflow **EcoATM_PWS.SUB_OfferBuyer_IsExcelDataSuccess** (Result: **$IsSuccessfullyImported**)**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.