# Microflow Detailed Specification: SUB_BidDataImport_GetValidBidData_Round3BidRank

### 📥 Inputs (Parameters)
- **$ImportFile** (Type: Custom_Excel_Import.ImportFile)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **ImportExcelData**
3. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataRankRound3ExportList**
   │ 1. 🔀 **DECISION:** `trim($IteratorBidData/ProductId) != ''`
   │    ➔ **If [false]:**
   │       1. **Remove **$$IteratorBidData** to/from list **$BidDataRankRound3ExportList****
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `trim($IteratorBidData/Grade) != ''`
   │          ➔ **If [false]:**
   │             1. **Remove **$$IteratorBidData** to/from list **$BidDataRankRound3ExportList****
   │          ➔ **If [true]:**
   │             1. 🔀 **DECISION:** `trim($IteratorBidData/Price) != ''`
   │                ➔ **If [true]:**
   │                   1. 🔀 **DECISION:** `$IteratorBidData/Price != '0' and $IteratorBidData/Price != '0.0'`
   │                      ➔ **If [true]:**
   │                      ➔ **If [false]:**
   │                         1. **Remove **$$IteratorBidData** to/from list **$BidDataRankRound3ExportList****
   │                ➔ **If [false]:**
   │                   1. **Remove **$$IteratorBidData** to/from list **$BidDataRankRound3ExportList****
   └─ **End Loop**
4. **LogMessage**
5. 🏁 **END:** Return `$BidDataRankRound3ExportList`

**Final Result:** This process concludes by returning a [List] value.