# Microflow Detailed Specification: SUB_CreateBidDataDownload_DW

### 📥 Inputs (Parameters)
- **$AgregatedInventoryList_DataWipe** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$AllBidDownloadList** (Type: AuctionUI.AllBidDownload)
- **$BidDataList** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. 🔄 **LOOP:** For each **$IteratorAggregatedInventory_DW** in **$AgregatedInventoryList_DataWipe**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventory_DW/EcoId` (Result: **$DeviceExistsCheck_DW**)**
   │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAggregatedInventory_DW/EcoId and $currentObject/Merged_Grade=$IteratorAggregatedInventory_DW/MergedGrade and ( ($currentObject/BidAmount != empty and $currentObject/BidAmount>0 ) or ($currentObject/BidQuantity != empty and $currentObject/BidQuantity>0) )` (Result: **$FilterBidData_1**)**
   │ 3. **List Operation: **Sort** on **$undefined** sorted by: BidDataId (Descending) (Result: **$SortedBidDataList**)**
   │ 4. **List Operation: **Head** on **$undefined** (Result: **$FindBidData_DW**)**
   │ 5. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAggregatedInventory_DW/EcoId and $currentObject/Merged_Grade=$IteratorAggregatedInventory_DW/MergedGrade and ( ($currentObject/BidAmount != empty and $currentObject/BidAmount>0 ) or ($currentObject/BidQuantity != empty and $currentObject/BidQuantity>0) )` (Result: **$FindBidData_DW_1**)**
   │ 6. 🔀 **DECISION:** `$DeviceExistsCheck_DW=empty`
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/MergedGrade='A_YYY'`
   │          ➔ **If [true]:**
   │             1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_A_YYY_DW** (Result: **$Created_A_YYY_DW**)**
   │             2. **Add **$$Created_A_YYY_DW** to/from list **$AllBidDownloadList****
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/MergedGrade='B_NYY/D_NNY'`
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/MergedGrade='C_YNY/G_YNN'`
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/MergedGrade='E_YYN'`
   │                            ➔ **If [true]:**
   │                               1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_E_YYN_DW** (Result: **$Created_E_YYN_DW**)**
   │                               2. **Add **$$Created_E_YYN_DW** to/from list **$AllBidDownloadList****
   │                            ➔ **If [false]:**
   │                               1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_F_NYN_H_NNN_DW** (Result: **$Created_F_NYN_H_NNN_DW**)**
   │                               2. **Add **$$Created_F_NYN_H_NNN_DW** to/from list **$AllBidDownloadList****
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_C_YNY_G_YNN_DW** (Result: **$Created_C_YNY_G_YNN_DW**)**
   │                         2. **Add **$$Created_C_YNY_G_YNN_DW** to/from list **$AllBidDownloadList****
   │                ➔ **If [true]:**
   │                   1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_B_NYY_D_NNY_DW** (Result: **$Created_B_NYY_D_NNY_DW**)**
   │                   2. **Add **$$Created_B_NYY_D_NNY_DW** to/from list **$AllBidDownloadList****
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/MergedGrade='A_YYY'`
   │          ➔ **If [true]:**
   │             1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_A_YYY_DW** (Result: **$AllBidDownload**)**
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/MergedGrade='B_NYY/D_NNY'`
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/MergedGrade='C_YNY/G_YNN'`
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/MergedGrade='E_YYN'`
   │                            ➔ **If [false]:**
   │                               1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_F_NYN_H_NNN_DW****
   │                            ➔ **If [true]:**
   │                               1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_E_YYN_DW****
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_C_YNY_G_YNN_DW****
   │                ➔ **If [true]:**
   │                   1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_B_NYY_D_NNY_DW** (Result: **$Variable**)**
   └─ **End Loop**
3. **Commit/Save **$AllBidDownloadList** to Database**
4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.