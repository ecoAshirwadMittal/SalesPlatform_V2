# Microflow Detailed Specification: SUB_BidDataImport_GetValidBidData

### 📥 Inputs (Parameters)
- **$ImportFile** (Type: Custom_Excel_Import.ImportFile)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **ImportExcelData**
3. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList**
   │ 1. 🔀 **DECISION:** `trim($IteratorBidData/ProductId) != ''`
   │    ➔ **If [false]:**
   │       1. **Remove **$$IteratorBidData** to/from list **$BidDataList****
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `trim($IteratorBidData/Grade) != ''`
   │          ➔ **If [false]:**
   │             1. **Remove **$$IteratorBidData** to/from list **$BidDataList****
   │          ➔ **If [true]:**
   │             1. 🔀 **DECISION:** `trim($IteratorBidData/Price) != ''`
   │                ➔ **If [true]:**
   │                   1. 🔀 **DECISION:** `$IteratorBidData/Price != '0' and $IteratorBidData/Price != '0.0'`
   │                      ➔ **If [true]:**
   │                      ➔ **If [false]:**
   │                         1. **Remove **$$IteratorBidData** to/from list **$BidDataList****
   │                ➔ **If [false]:**
   │                   1. **Remove **$$IteratorBidData** to/from list **$BidDataList****
   └─ **End Loop**
4. **LogMessage**
5. 🏁 **END:** Return `$BidDataList`

**Final Result:** This process concludes by returning a [List] value.