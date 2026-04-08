# Microflow Detailed Specification: SUB_ImportCreatePODetails

### 📥 Inputs (Parameters)
- **$PurchaseOrder** (Type: EcoATM_PO.PurchaseOrder)
- **$POHelper** (Type: EcoATM_PO.POHelper)
- **$PurchaseOrderDoc** (Type: EcoATM_PO.PurchaseOrderDoc)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Update **$POHelper**
      - Set **MissingBuyerCodeValidation** = `false`
      - Set **MissingBuyerCodeList** = `''`
      - Set **InvalidFileValidation** = `false`
      - Set **InValidPOPeriod** = `false`**
3. **Retrieve related **PurchaseOrder_Week_From** via Association from **$PurchaseOrder** (Result: **$FromWeek**)**
4. **Retrieve related **PurchaseOrder_Week_To** via Association from **$PurchaseOrder** (Result: **$ToWeek**)**
5. **Call Microflow **EcoATM_PO.VAL_WeekRange_PO** (Result: **$isWeekRange_Valid**)**
6. 🔀 **DECISION:** `$isWeekRange_Valid`
   ➔ **If [true]:**
      1. **ImportExcelData**
      2. **Call Microflow **EcoATM_PO.VAL_BuyerCode_PO** (Result: **$isBuyerCode_Valid**)**
      3. 🔀 **DECISION:** `$isBuyerCode_Valid`
         ➔ **If [true]:**
            1. **AggregateList**
            2. **CreateList**
            3. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode**  (Result: **$BuyerCodeList**)**
            4. 🔄 **LOOP:** For each **$IteratorMWPO_REPORT** in **$MWPO_REPORTList**
               │ 1. **List Operation: **Find** on **$undefined** where `$IteratorMWPO_REPORT/BuyerCode` (Result: **$BuyerCodeFromDB**)**
               │ 2. **Call Microflow **EcoATM_PO.SUB_CreatePODetail** (Result: **$NewPODetail**)**
               │ 3. **Add **$$NewPODetail
** to/from list **$PODetailList****
               └─ **End Loop**
            5. **Update **$PurchaseOrder**
      - Set **TotalRecords** = `$Count`**
            6. **Commit/Save **$PurchaseOrder** to Database**
            7. **Commit/Save **$PODetailList** to Database**
            8. **DB Retrieve **EcoATM_MDM.Week** Filter: `[$FromWeek/WeekStartDateTime<=WeekStartDateTime and $ToWeek/WeekStartDateTime>=WeekStartDateTime]` (Result: **$NewPOWeekList**)**
            9. **CreateList**
            10. 🔄 **LOOP:** For each **$IteratorWeek** in **$NewPOWeekList**
               │ 1. **Create **EcoATM_PO.WeekPeriod** (Result: **$NewWeekPeriod**)
      - Set **WeekPeriod_Week** = `$IteratorWeek`
      - Set **WeekPeriod_PurchaseOrder** = `$PurchaseOrder`**
               │ 2. **Add **$$NewWeekPeriod
** to/from list **$WeekPeriodList_toCommit****
               └─ **End Loop**
            11. **Commit/Save **$WeekPeriodList_toCommit** to Database**
            12. **Call Microflow **EcoATM_PO.SUB_UploadPOToSnowFlake****
            13. **Close current page/popup**
            14. **Maps to Page: **EcoATM_PO.PurchaseOrder_Confirmation****
            15. **Maps to Page: **EcoATM_PO.PurchaseOrder_Main****
            16. **Call Microflow **EcoATM_PO.NAV_CreatePO****
            17. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            18. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$POHelper** (and Save to DB)
      - Set **MissingBuyerCodeValidation** = `true`
      - Set **FileName** = `$PurchaseOrderDoc/Name`**
            2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$POHelper**
      - Set **InValidPOPeriod** = `true`
      - Set **FileName** = `$PurchaseOrderDoc/Name`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.