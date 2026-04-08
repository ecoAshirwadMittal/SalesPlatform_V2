# Microflow Detailed Specification: SUB_Offers_Download_SimilarSKU

### 📥 Inputs (Parameters)
- **$OfferDrawerHelper** (Type: EcoATM_PWS.OfferDrawerHelper)
- **$OfferItem** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.DS_OfferDrawer_SimilarSKUs** (Result: **$OfferItemList**)**
2. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name = 'OfferDrawerSimilarSKU']` (Result: **$MxTemplate**)**
3. **Create **EcoATM_PWS.OfferItemExcelDocument** (Result: **$OfferItemExcelDocument**)**
4. **CreateList**
5. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Create **EcoATM_PWS.OfferItemDataExport** (Result: **$NewOfferItemDataExport**)
      - Set **OfferDate** = `formatDateTime(trimToDays($IteratorOfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferSubmissionDate), 'MM/dd/yyyy')`
      - Set **Status** = `toString($IteratorOfferItem/OfferDrawerStatus)`
      - Set **Customer** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **Qty** = `$IteratorOfferItem/OfferQuantity`
      - Set **ListPrice** = `'$'+$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice`
      - Set **OfferPrice** = `'$'+$IteratorOfferItem/OfferPrice`
      - Set **Color** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Color/EcoATM_PWSMDM.Color/DisplayName`
      - Set **SKU** = `$IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU`
      - Set **OfferItemDataExport_OfferItemExcelDocument** = `$OfferItemExcelDocument`**
   │ 2. **Add **$$NewOfferItemDataExport** to/from list **$OfferItemDataExportList****
   └─ **End Loop**
6. **Commit/Save **$OfferItemDataExportList** to Database**
7. **JavaCallAction**
8. **Update **$Document** (and Save to DB)
      - Set **Name** = `'SimilarSKUOffer.xlsx'`**
9. **DownloadFile**
10. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.