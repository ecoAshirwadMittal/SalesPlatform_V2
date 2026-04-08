# Microflow Detailed Specification: SUB_CreateBidDataDownload_NonDW

### 📥 Inputs (Parameters)
- **$AggregatedInventoryList** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$AllBidDownloadList** (Type: AuctionUI.AllBidDownload)
- **$BidDataList** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. 🔄 **LOOP:** For each **$IteratorAggregatedInventory** in **$AggregatedInventoryList**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventory/EcoId` (Result: **$DeviceExistsCheck**)**
   │ 2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAggregatedInventory/EcoId and $currentObject/Merged_Grade=$IteratorAggregatedInventory/MergedGrade and ( ($currentObject/BidAmount != empty and $currentObject/BidAmount>0 ) or ($currentObject/BidQuantity != empty and $currentObject/BidQuantity>0) )` (Result: **$FilterBidData_1**)**
   │ 3. **List Operation: **Sort** on **$undefined** sorted by: BidDataId (Descending) (Result: **$SortedBidDataList**)**
   │ 4. **List Operation: **Head** on **$undefined** (Result: **$FilterBidData**)**
   │ 5. 🔀 **DECISION:** `$DeviceExistsCheck=empty`
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='A_YYY'`
   │          ➔ **If [true]:**
   │             1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_A_YYY** (Result: **$Created_A_YYY**)**
   │             2. **Add **$$Created_A_YYY** to/from list **$AllBidDownloadList****
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='B_NYY/D_NNY'`
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='C_YNY/G_YNN'`
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='E_YYN'`
   │                            ➔ **If [true]:**
   │                               1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_E_YYN** (Result: **$Created_E_YYN**)**
   │                               2. **Add **$$Created_E_YYN** to/from list **$AllBidDownloadList****
   │                            ➔ **If [false]:**
   │                               1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_F_NYN_H_NNN** (Result: **$Created_F_NYN_H_NNN**)**
   │                               2. **Add **$$Created_F_NYN_H_NNN** to/from list **$AllBidDownloadList****
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_C_YNY_G_YNN** (Result: **$Created_C_YNY_G_YNN**)**
   │                         2. **Add **$$Created_C_YNY_G_YNN** to/from list **$AllBidDownloadList****
   │                ➔ **If [true]:**
   │                   1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_AllBidDownload_Create_B_NYY_D_NNY** (Result: **$Created_B_NYY_D_NNY**)**
   │                   2. **Add **$$Created_B_NYY_D_NNY** to/from list **$AllBidDownloadList****
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='A_YYY'`
   │          ➔ **If [true]:**
   │             1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_A_YYY****
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='B_NYY/D_NNY'`
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='C_YNY/G_YNN'`
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='E_YYN'`
   │                            ➔ **If [false]:**
   │                               1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_F_NYN_H_NNN** (Result: **$Variable**)**
   │                            ➔ **If [true]:**
   │                               1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_E_YYN****
   │                      ➔ **If [true]:**
   │                         1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_C_YNY_G_YNN** (Result: **$Variable**)**
   │                ➔ **If [true]:**
   │                   1. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_Change_ExistingDevice_B_NYY_D_NNY****
   └─ **End Loop**
3. **Commit/Save **$AllBidDownloadList** to Database**
4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.