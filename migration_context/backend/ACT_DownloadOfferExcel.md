# Microflow Detailed Specification: ACT_DownloadOfferExcel

### 📥 Inputs (Parameters)
- **$OfferMasterHelper** (Type: EcoATM_PWS.OfferMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'DownloadOfferExcel'`**
2. **Create Variable **$Description** = `'Download offer excel file: ' + toString($OfferMasterHelper/StatusSelected)`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Call Microflow **EcoATM_PWS.SUB_ListOffersForDownload** (Result: **$OfferList**)**
5. **Call Microflow **EcoATM_PWS.SUB_GetOfferExcelFileName** (Result: **$FileName**)**
6. **Create **EcoATM_PWS.OfferExcelDocument** (Result: **$NewOfferDownload**)
      - Set **DeleteAfterDownload** = `true`**
7. **CreateList**
8. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
   │ 1. **Create **EcoATM_PWS.OfferDataExport** (Result: **$NewOfferDataExport**)
      - Set **OfferDataExport_OfferExcelDocument** = `$NewOfferDownload`
      - Set **OfferID** = `$IteratorOffer/OfferID`
      - Set **OfferStatus** = `($IteratorOffer/EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus/SystemStatus)`
      - Set **BuyerName** = `$IteratorOffer/EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **SalesRep** = `if($IteratorOffer/EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_BuyerManagement.Buyer_SalesRepresentative/EcoATM_BuyerManagement.SalesRepresentative/SalesRepFirstName != empty) then $IteratorOffer/EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_BuyerManagement.Buyer_SalesRepresentative/EcoATM_BuyerManagement.SalesRepresentative/SalesRepFirstName + ' ' + $IteratorOffer/EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_BuyerManagement.Buyer_SalesRepresentative/EcoATM_BuyerManagement.SalesRepresentative/SalesRepLastName else ''`
      - Set **SKUs** = `if $OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review or $OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Declined then $IteratorOffer/OfferSKUCount else $IteratorOffer/FinalOfferTotalSKU`
      - Set **Qty** = `if $OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review or $OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Declined then $IteratorOffer/OfferTotalQuantity else $IteratorOffer/FinalOfferTotalQty`
      - Set **OfferPrice** = `if $OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review or $OfferMasterHelper/StatusSelected = EcoATM_PWS.ENUM_PWSOrderStatus.Declined then $IteratorOffer/OfferTotalPrice else $IteratorOffer/FinalOfferTotalPrice`
      - Set **OfferDate** = `$IteratorOffer/OfferSubmissionDate`
      - Set **LastUpdated** = `$IteratorOffer/UpdateDate`
      - Set **BuyerCode** = `$IteratorOffer/EcoATM_PWS.Offer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code`**
   │ 2. **Add **$$NewOfferDataExport** to/from list **$OfferDataExportList****
   └─ **End Loop**
9. **Commit/Save **$OfferDataExportList** to Database**
10. **DB Retrieve **XLSReport.MxTemplate** Filter: `[ ( Name = 'OfferDownload' ) ]` (Result: **$MxTemplate**)**
11. **JavaCallAction**
12. **Update **$OfferExcelFile** (and Save to DB)
      - Set **Name** = `$FileName`**
13. **DownloadFile**
14. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
15. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.