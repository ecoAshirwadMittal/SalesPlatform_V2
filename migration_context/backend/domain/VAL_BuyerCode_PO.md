# Microflow Detailed Specification: VAL_BuyerCode_PO

### 📥 Inputs (Parameters)
- **$PODetailsList** (Type: EcoATM_PO.PODetails_NP)
- **$POHelper** (Type: EcoATM_PO.POHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Create Variable **$isBuyerCodeValid** = `true`**
3. **Create Variable **$MissingBuyerCodes** = `''`**
4. **CreateList**
5. 🔄 **LOOP:** For each **$IteratorPOList** in **$PODetailsList**
   │ 1. 🔀 **DECISION:** `trim($IteratorPOList/BuyerCode)!= ''`
   │    ➔ **If [true]:**
   │       1. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[Status='Active' and Code=$IteratorPOList/BuyerCode]` (Result: **$BuyerCodeDB**)**
   │       2. 🔀 **DECISION:** `$BuyerCodeDB=empty`
   │          ➔ **If [false]:**
   │          ➔ **If [true]:**
   │             1. **Update Variable **$isBuyerCodeValid** = `false`**
   │             2. 🔀 **DECISION:** `contains($MissingBuyerCodes,$IteratorPOList/BuyerCode)`
   │                ➔ **If [true]:**
   │                ➔ **If [false]:**
   │                   1. **Update Variable **$MissingBuyerCodes** = `if $MissingBuyerCodes!='' then $MissingBuyerCodes+','+$IteratorPOList/BuyerCode else $IteratorPOList/BuyerCode`**
   │    ➔ **If [false]:**
   │       1. **Update Variable **$isBuyerCodeValid** = `false`**
   └─ **End Loop**
6. **Update **$POHelper**
      - Set **MissingBuyerCodeList** = `$MissingBuyerCodes`**
7. **LogMessage**
8. 🏁 **END:** Return `$isBuyerCodeValid`

**Final Result:** This process concludes by returning a [Boolean] value.