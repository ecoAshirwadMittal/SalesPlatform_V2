# Microflow Detailed Specification: ACT_Round3_BidData_Import_ClientController_PreProcess

### 📥 Inputs (Parameters)
- **$RoundThreeBidDataExcelExport** (Type: AuctionUI.RoundThreeBidDataExcelExport)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)
- **$RoundThreeBuyersDataReport** (Type: AuctionUI.RoundThreeBuyersDataReport)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Update **$RoundThreeBidDataExcelExport**
      - Set **ErrorMessage** = `empty`**
3. 🔀 **DECISION:** `$RoundThreeBidDataExcelExport/Name != empty`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_Round3_ExcelImport_PreProcess** (Result: **$ExcelImport_BidDataRound3**)**
      2. 🔀 **DECISION:** `$ExcelImport_BidDataRound3 != empty`
         ➔ **If [true]:**
            1. **CreateList**
            2. **Call Microflow **AuctionUI.SUB_Round3_BidData_TransformAndCommit_UsingPreProcessedData** (Result: **$BidData_ToCOmmit**)**
            3. 🔀 **DECISION:** `$BidData_ToCOmmit != empty`
               ➔ **If [true]:**
                  1. **AggregateList**
                  2. **Close current page/popup**
                  3. **Commit/Save **$BidRoundList** to Database**
                  4. **Commit/Save **$BidData_ToCOmmit** to Database**
                  5. **Delete**
                  6. 🔄 **LOOP:** For each **$IteratorBidRound** in **$BidRoundList**
                     │ 1. **Call Microflow **AuctionUI.ACT_Round3_SubmitBidData****
                     └─ **End Loop**
                  7. **Update **$RoundThreeBuyersDataReport** (and Save to DB)
      - Set **SubmittedBy** = `$currentUser/Name`
      - Set **SubmittedOn** = `[%CurrentDateTime%]`**
                  8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  9. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  2. 🔀 **DECISION:** `$SchedulingAuction/Round = 3`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Update **$RoundThreeBidDataExcelExport**
      - Set **ErrorMessage** = `'Bids or Quantity were lower than existing values'`**
                        2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$RoundThreeBidDataExcelExport**
      - Set **ErrorMessage** = `'There were no records to process'`**
            2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. **Update **$RoundThreeBidDataExcelExport**
      - Set **ErrorMessage** = `'Import file cannot be empty, please select a file and try again.'`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.