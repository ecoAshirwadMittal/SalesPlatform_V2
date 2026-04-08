# Nanoflow: NAN_UploadExcelFileContent_Close

> SPKB-291 - PWS 1.2 - Upload Order

**Allowed Roles:** EcoATM_PWS.Administrator, EcoATM_PWS.SalesOps, EcoATM_PWS.SalesRep

## 📥 Inputs

- **$OfferExcelImportDocument** (EcoATM_PWS.ManageFileDocument)
- **$BuyerCode** (EcoATM_BuyerManagement.BuyerCode)

## ⚙️ Execution Flow

1. **Close current page/popup**
2. **Delete **$OfferExcelImportDocument** from Database**
3. **Open Page: **EcoATM_PWS.PWSOrder_PE****
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
