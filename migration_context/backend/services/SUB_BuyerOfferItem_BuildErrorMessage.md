# Microflow Detailed Specification: SUB_BuyerOfferItem_BuildErrorMessage

### 📥 Inputs (Parameters)
- **$UnresolvedSKUList** (Type: EcoATM_PWS.OfferDataExcelImporter)
- **$DuplicateSKUList** (Type: EcoATM_PWS.OfferDataExcelImporter)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `'Data requiring fix:'`**
2. 🔄 **LOOP:** For each **$IteratorDuplicateSKU** in **$DuplicateSKUList**
   │ 1. **Update Variable **$Message** = `$Message+' Duplicate Device SKU:'+$IteratorDuplicateSKU/SKU`**
   └─ **End Loop**
3. 🔄 **LOOP:** For each **$IteratorUnresolvedSKU** in **$UnresolvedSKUList**
   │ 1. **Update Variable **$Message** = `$Message+' Unknown Device SKU:'+$IteratorUnresolvedSKU/SKU`**
   └─ **End Loop**
4. 🏁 **END:** Return `$Message`

**Final Result:** This process concludes by returning a [String] value.