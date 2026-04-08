# Microflow Detailed Specification: DS_GetRMASummaryByStatus

### 📥 Inputs (Parameters)
- **$RMAMasterHelper** (Type: EcoATM_RMA.RMAMasterHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **RMAUiHelper_RMAMasterHelper** via Association from **$RMAMasterHelper** (Result: **$RMAUiHelperList**)**
2. 🔀 **DECISION:** `$RMAUiHelperList != empty`
   ➔ **If [true]:**
      1. 🔄 **LOOP:** For each **$IteratorRMAUiHelper** in **$RMAUiHelperList**
         │ 1. 🔀 **DECISION:** `$IteratorRMAUiHelper/HeaderLabel != empty`
         │    ➔ **If [true]:**
         │       1. **DB Retrieve **EcoATM_RMA.RMA** Filter: `[EcoATM_RMA.RMA_RMAStatus/EcoATM_RMA.RMAStatus/StatusGroupedTo = $IteratorRMAUiHelper/HeaderLabel]` (Result: **$RMAList**)**
         │       2. **AggregateList**
         │       3. **AggregateList**
         │       4. **AggregateList**
         │       5. **AggregateList**
         │       6. **Update **$IteratorRMAUiHelper**
      - Set **TotalPrice** = `$TotalPrice`
      - Set **TotalSKUs** = `$TotalSKUs`
      - Set **TotalQty** = `$TotalQty`
      - Set **RMACount** = `$TotalRMAs`**
         │    ➔ **If [false]:**
         │       1. **DB Retrieve **EcoATM_RMA.RMA**  (Result: **$RMAList_All**)**
         │       2. **AggregateList**
         │       3. **AggregateList**
         │       4. **AggregateList**
         │       5. **AggregateList**
         │       6. **Update **$IteratorRMAUiHelper**
      - Set **TotalPrice** = `$TotalPrice_All`
      - Set **TotalSKUs** = `$TotalSKUs_All`
      - Set **TotalQty** = `$TotalQty_All`
      - Set **RMACount** = `$TotalRMAs_All`**
         └─ **End Loop**
      2. **List Operation: **Find** on **$undefined** where `$RMAMasterHelper` (Result: **$RMAUiHelper_Selected**)**
      3. 🔀 **DECISION:** `$RMAUiHelper_Selected != empty`
         ➔ **If [true]:**
            1. **List Operation: **Sort** on **$undefined** sorted by: SortOrder (Ascending) (Result: **$RMAUiHelperList_Sorted**)**
            2. **Commit/Save **$RMAUiHelperList_Sorted** to Database**
            3. 🏁 **END:** Return `$RMAUiHelperList_Sorted`
         ➔ **If [false]:**
            1. **DB Retrieve **EcoATM_RMA.RMAStatus** Filter: `[IsDefaultStatus]` (Result: **$RMAStatus_Default**)**
            2. **List Operation: **Head** on **$undefined** (Result: **$RMAUiHelper_Default**)**
            3. 🔀 **DECISION:** `$RMAUiHelper_Default != empty`
               ➔ **If [true]:**
                  1. **Update **$RMAUiHelper_Default**
      - Set **RMAUiHelper_RMAMasterHelper_Selected** = `$RMAMasterHelper`**
                  2. **List Operation: **Sort** on **$undefined** sorted by: SortOrder (Ascending) (Result: **$RMAUiHelperList_Sorted**)**
                  3. **Commit/Save **$RMAUiHelperList_Sorted** to Database**
                  4. 🏁 **END:** Return `$RMAUiHelperList_Sorted`
               ➔ **If [false]:**
                  1. **List Operation: **Head** on **$undefined** (Result: **$RMAUiHelper_Head**)**
                  2. **Update **$RMAUiHelper_Head**
      - Set **RMAUiHelper_RMAMasterHelper_Selected** = `$RMAMasterHelper`**
                  3. **List Operation: **Sort** on **$undefined** sorted by: SortOrder (Ascending) (Result: **$RMAUiHelperList_Sorted**)**
                  4. **Commit/Save **$RMAUiHelperList_Sorted** to Database**
                  5. 🏁 **END:** Return `$RMAUiHelperList_Sorted`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.