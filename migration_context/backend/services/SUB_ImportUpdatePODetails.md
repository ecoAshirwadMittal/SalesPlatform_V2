# Microflow Detailed Specification: SUB_ImportUpdatePODetails

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
3. **ImportExcelData**
4. **Call Microflow **EcoATM_PO.VAL_BuyerCode_PO** (Result: **$isBuyerCode_Valid**)**
5. 🔀 **DECISION:** `$isBuyerCode_Valid`
   ➔ **If [true]:**
      1. **Retrieve related **PODetail_PurchaseOrder** via Association from **$PurchaseOrder** (Result: **$PODetailList_Old**)**
      2. **Delete**
      3. **AggregateList**
      4. **CreateList**
      5. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode**  (Result: **$BuyerCodeList**)**
      6. 🔄 **LOOP:** For each **$IteratorMWPO_REPORT** in **$MWPO_REPORTList**
         │ 1. **List Operation: **Find** on **$undefined** where `$IteratorMWPO_REPORT/BuyerCode` (Result: **$BuyerCodeFromDB**)**
         │ 2. **Call Microflow **EcoATM_PO.SUB_CreatePODetail** (Result: **$NewPODetail**)**
         │ 3. **Add **$$NewPODetail
** to/from list **$PODetailList****
         └─ **End Loop**
      7. **Commit/Save **$PODetailList** to Database**
      8. **Call Microflow **EcoATM_PO.SUB_UploadPOToSnowFlake****
      9. **Close current page/popup**
      10. **Maps to Page: **EcoATM_PO.PurchaseOrder_Main****
      11. **Show Message (Information): `Imported {1} records`**
      12. **Call Microflow **EcoATM_PO.NAV_CreatePO****
      13. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      14. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$POHelper**
      - Set **MissingBuyerCodeValidation** = `true`
      - Set **FileName** = `$PurchaseOrderDoc/Name`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.