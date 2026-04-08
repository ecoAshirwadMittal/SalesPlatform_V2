# Microflow Detailed Specification: SUB_Round3_ExcelImport_PreProcess

### 📥 Inputs (Parameters)
- **$RoundThreeBidDataExcelExport** (Type: AuctionUI.RoundThreeBidDataExcelExport)
- **$RoundThreeBuyersDataReport** (Type: AuctionUI.RoundThreeBuyersDataReport)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **ImportExcelData**
3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/Accept_Max_Bid_YN != empty and toUpperCase($currentObject/Accept_Max_Bid_YN) != 'N' and trim($currentObject/Accept_Max_Bid_YN) != ''` (Result: **$BidData_Round3List_Filtered**)**
4. **Retrieve related **RoundThreeBidDataExcelExport_RoundThreeBidDataReport** via Association from **$RoundThreeBidDataExcelExport** (Result: **$RoundThreeBidDataReportList**)**
5. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidData_Round3List_Filtered**
   │ 1. **Create Variable **$Code** = `parseInteger(substring($IteratorBidData/Code_Grade, 0, find($IteratorBidData/Code_Grade, ' ')))`**
   │ 2. **Create Variable **$MergedGrade** = `substring($IteratorBidData/Code_Grade, find($IteratorBidData/Code_Grade, ' ') + 1)`**
   │ 3. 🔀 **DECISION:** `trim($IteratorBidData/Code_Grade) != ''`
   │    ➔ **If [false]:**
   │       1. **Remove **$$IteratorBidData** to/from list **$BidData_Round3List_Filtered****
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `trim($MergedGrade) != ''`
   │          ➔ **If [false]:**
   │             1. **Remove **$$IteratorBidData** to/from list **$BidData_Round3List_Filtered****
   │          ➔ **If [true]:**
   │             1. 🔀 **DECISION:** `trim($IteratorBidData/Bid) != ''`
   │                ➔ **If [false]:**
   │                   1. **Remove **$$IteratorBidData** to/from list **$BidData_Round3List_Filtered****
   │                ➔ **If [true]:**
   │                   1. 🔀 **DECISION:** `trim($IteratorBidData/Code) != ''`
   │                      ➔ **If [false]:**
   │                         1. **Remove **$$IteratorBidData** to/from list **$BidData_Round3List_Filtered****
   │                      ➔ **If [true]:**
   │                         1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/CodeGrade = $IteratorBidData/Code_Grade and $currentObject/BuyerCode = $IteratorBidData/Code` (Result: **$RoundThreeBidDataReport**)**
   │                         2. 🔀 **DECISION:** `$RoundThreeBidDataReport != empty`
   │                            ➔ **If [true]:**
   │                               1. **Update **$RoundThreeBidDataReport**
      - Set **MaxBid** = `$IteratorBidData/Accept_Max_Bid_YN`**
   │                            ➔ **If [false]:**
   └─ **End Loop**
6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
7. 🏁 **END:** Return `$BidData_Round3List_Filtered`

**Final Result:** This process concludes by returning a [List] value.