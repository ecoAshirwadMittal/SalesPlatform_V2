# Microflow Detailed Specification: VAL_OfferBuyerExcelFile_IsValid

### 📥 Inputs (Parameters)
- **$OfferExcelImportDocument** (Type: EcoATM_PWS.ManageFileDocument)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$OfferExcelImportDocument!=empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$OfferExcelImportDocument/HasContents`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.