# Microflow Detailed Specification: SUB_CreatePODetail

### 📥 Inputs (Parameters)
- **$IteratorMWPO_REPORT** (Type: EcoATM_PO.PODetails_NP)
- **$PurchaseOrder** (Type: EcoATM_PO.PurchaseOrder)
- **$BuyerCodeFromDB** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **EcoATM_PO.PODetail** (Result: **$NewPODetail**)
      - Set **ProductID** = `$IteratorMWPO_REPORT/ProductID`
      - Set **Grade** = `$IteratorMWPO_REPORT/Grade`
      - Set **ModelName** = `$IteratorMWPO_REPORT/ModelName`
      - Set **Price** = `$IteratorMWPO_REPORT/Price`
      - Set **QtyCap** = `if($IteratorMWPO_REPORT/QtyCap!=empty and trim($IteratorMWPO_REPORT/QtyCap)!='') then parseInteger($IteratorMWPO_REPORT/QtyCap) else empty`
      - Set **PODetail_PurchaseOrder** = `$PurchaseOrder`
      - Set **PODetail_BuyerCode** = `$BuyerCodeFromDB`
      - Set **TempBuyerCode** = `$BuyerCodeFromDB/Code`**
2. 🏁 **END:** Return `$NewPODetail`

**Final Result:** This process concludes by returning a [Object] value.