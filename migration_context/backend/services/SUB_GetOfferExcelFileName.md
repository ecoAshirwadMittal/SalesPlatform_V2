# Microflow Detailed Specification: SUB_GetOfferExcelFileName

### 📥 Inputs (Parameters)
- **$OfferMasterHelper** (Type: EcoATM_PWS.OfferMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$FileName** = `'PWS Offers_'`**
2. 🔀 **DECISION:** `$OfferMasterHelper/StatusSelected = empty`
   ➔ **If [true]:**
      1. **Update Variable **$FileName** = `$FileName + 'Total_'`**
      2. **Update Variable **$FileName** = `$FileName + formatDateTime([%CurrentDateTime%],'yyyyMMdd')+'.xlsx'`**
      3. 🏁 **END:** Return `$FileName`
   ➔ **If [false]:**
      1. **Update Variable **$FileName** = `$FileName + toString($OfferMasterHelper/StatusSelected) +'_'`**
      2. **Update Variable **$FileName** = `$FileName + formatDateTime([%CurrentDateTime%],'yyyyMMdd')+'.xlsx'`**
      3. 🏁 **END:** Return `$FileName`

**Final Result:** This process concludes by returning a [String] value.