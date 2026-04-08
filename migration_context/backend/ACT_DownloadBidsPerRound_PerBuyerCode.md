# Microflow Detailed Specification: ACT_DownloadBidsPerRound_PerBuyerCode

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$BuyerCode** (Type: AuctionUI.BuyerCode)
- **$FinalBuyerCode** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **DB Retrieve **AuctionUI.AllBidDownload_ScreenHelper**  (Result: **$AllBidDownload_ScreenHelper**)**
3. **Update **$AllBidDownload_ScreenHelper**
      - Set **R1Caption** = `if $SchedulingAuction/Round=1 then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R1Caption`
      - Set **R2Caption** = `if $SchedulingAuction/Round=2 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R2Caption`
      - Set **UpsellCaption** = `if $SchedulingAuction/Round=3 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/UpsellCaption`**
4. **JavaCallAction**
5. **Update **$AllBidDownload_ScreenHelper**
      - Set **R1Caption** = `if $SchedulingAuction/Round=1 then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R1Caption`
      - Set **R2Caption** = `if $SchedulingAuction/Round=2 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/R2Caption`
      - Set **UpsellCaption** = `if $SchedulingAuction/Round=3 and $SchedulingAuction/IsActive then 'Bid Files generation in progress...' else $AllBidDownload_ScreenHelper/UpsellCaption`**
6. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
7. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
8. **CreateList**
9. **Add **$$BuyerCode** to/from list **$BuyerCodeList****
10. **CreateList**
11. **DB Retrieve **XLSReport.MxTemplate** Filter: `[Name='AllBids_by_BuyerCode']` (Result: **$MxTemplate**)**
12. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregatedInventoryList**)**
13. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ AuctionUI.AggregatedInventory_Week=$Week and Data_Wipe_Quantity > 0 ]` (Result: **$AgregatedInventoryList_DataWipe**)**
14. **CreateList**
15. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │ 1. **LogMessage**
   │ 2. **CreateList**
   │ 3. **Retrieve related **BidData_BuyerCode** via Association from **$IteratorBuyerCode** (Result: **$BidDataList**)**
   │ 4. 🔀 **DECISION:** `$IteratorBuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
   │    ➔ **If [false]:**
   │       1. 🔄 **LOOP:** For each **$IteratorAggregatedInventory** in **$AggregatedInventoryList**
   │          │ 1. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventory/ecoID` (Result: **$DeviceExistsCheck**)**
   │          │ 2. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventory` (Result: **$FindBidData**)**
   │          │ 3. 🔀 **DECISION:** `$DeviceExistsCheck=empty`
   │          │    ➔ **If [true]:**
   │          │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory/Merged_Grade='A_YYY'`
   │          │          ➔ **If [true]:**
   │          │             1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateA_YYY**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **A_YYY** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeA_YYY** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **A_YYYEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **A_YYYQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │             2. **Add **$$CreateA_YYY** to/from list **$AllBidDownloadList****
   │          │          ➔ **If [false]:**
   │          │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory/Merged_Grade='B_NYY/D_NNY'`
   │          │                ➔ **If [false]:**
   │          │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory/Merged_Grade='C_YNY/G_YNN'`
   │          │                      ➔ **If [false]:**
   │          │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory/Merged_Grade='E_YYN'`
   │          │                            ➔ **If [true]:**
   │          │                               1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateE_YYN**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **E_YYN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeE_YYN** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **E_YYNEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **E_YYNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │                               2. **Add **$$CreateE_YYN** to/from list **$AllBidDownloadList****
   │          │                            ➔ **If [false]:**
   │          │                               1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateF_NYN_H_NNN**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **F_NYN_H_NNN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeF_NYN_H_NNN** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **F_NYN_H_NNNEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **F_NYN_H_NNNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │                               2. **Add **$$CreateF_NYN_H_NNN** to/from list **$AllBidDownloadList****
   │          │                      ➔ **If [true]:**
   │          │                         1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateC_YNY_G_YNN**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **C_YNY_G_YNN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeC_YNY_G_YNN** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **C_YNY_G_YNNEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **C_YNY_G_YNNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │                         2. **Add **$$CreateC_YNY_G_YNN** to/from list **$AllBidDownloadList****
   │          │                ➔ **If [true]:**
   │          │                   1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateB_NYY_D_NNY**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **B_NYY_D_NNY** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeB_NYY_D_NNY** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **B_NYY_D_NNYEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **B_NYY_D_NNYQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │                   2. **Add **$$CreateB_NYY_D_NNY** to/from list **$AllBidDownloadList****
   │          │    ➔ **If [false]:**
   │          │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory/Merged_Grade='A_YYY'`
   │          │          ➔ **If [true]:**
   │          │             1. **Update **$DeviceExistsCheck**
      - Set **A_YYY** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeA_YYY** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **A_YYYEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **A_YYYQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │          ➔ **If [false]:**
   │          │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory/Merged_Grade='B_NYY/D_NNY'`
   │          │                ➔ **If [false]:**
   │          │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory/Merged_Grade='C_YNY/G_YNN'`
   │          │                      ➔ **If [false]:**
   │          │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory/Merged_Grade='E_YYN'`
   │          │                            ➔ **If [false]:**
   │          │                               1. **Update **$DeviceExistsCheck**
      - Set **F_NYN_H_NNN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeF_NYN_H_NNN** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **F_NYN_H_NNNEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **F_NYN_H_NNNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │                            ➔ **If [true]:**
   │          │                               1. **Update **$DeviceExistsCheck**
      - Set **E_YYN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeE_YYN** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **E_YYNEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **E_YYNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │                      ➔ **If [true]:**
   │          │                         1. **Update **$DeviceExistsCheck**
      - Set **C_YNY_G_YNN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeC_YNY_G_YNN** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **C_YNY_G_YNNEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **C_YNY_G_YNNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          │                ➔ **If [true]:**
   │          │                   1. **Update **$DeviceExistsCheck**
      - Set **B_NYY_D_NNY** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeB_NYY_D_NNY** = `'$'+toString($IteratorAggregatedInventory/TargetPrice)`
      - Set **B_NYY_D_NNYEstimatedQuantity** = `$IteratorAggregatedInventory/Total_Quantity`
      - Set **B_NYY_D_NNYQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          └─ **End Loop**
   │       2. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$AllBidsZipTempList**)**
   │    ➔ **If [true]:**
   │       1. 🔄 **LOOP:** For each **$IteratorAggregatedInventory_DW** in **$AgregatedInventoryList_DataWipe**
   │          │ 1. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventory_DW/ecoID` (Result: **$DeviceExistsCheck_DW**)**
   │          │ 2. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventory_DW` (Result: **$FindBidData_DW**)**
   │          │ 3. 🔀 **DECISION:** `$DeviceExistsCheck_DW=empty`
   │          │    ➔ **If [true]:**
   │          │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/Merged_Grade='A_YYY'`
   │          │          ➔ **If [true]:**
   │          │             1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateA_YYY_DW**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory_DW/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory_DW/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory_DW/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory_DW/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory_DW/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **A_YYY** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeA_YYY** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **A_YYYEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **A_YYYQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │             2. **Add **$$CreateA_YYY_DW** to/from list **$AllBidDownloadList****
   │          │          ➔ **If [false]:**
   │          │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/Merged_Grade='B_NYY/D_NNY'`
   │          │                ➔ **If [false]:**
   │          │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/Merged_Grade='C_YNY/G_YNN'`
   │          │                      ➔ **If [false]:**
   │          │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/Merged_Grade='E_YYN'`
   │          │                            ➔ **If [true]:**
   │          │                               1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateE_YYN_DW**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory_DW/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory_DW/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory_DW/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory_DW/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory_DW/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **E_YYN** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeE_YYN** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **E_YYNEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **E_YYNQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │                               2. **Add **$$CreateE_YYN_DW** to/from list **$AllBidDownloadList****
   │          │                            ➔ **If [false]:**
   │          │                               1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateF_NYN_H_NNN_DW**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory_DW/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory_DW/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory_DW/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory_DW/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory_DW/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **F_NYN_H_NNN** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeF_NYN_H_NNN** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **F_NYN_H_NNNEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **F_NYN_H_NNNQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │                               2. **Add **$$CreateF_NYN_H_NNN_DW** to/from list **$AllBidDownloadList****
   │          │                      ➔ **If [true]:**
   │          │                         1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateC_YNY_G_YNN_DW**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory_DW/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory_DW/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory_DW/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory_DW/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory_DW/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **C_YNY_G_YNN** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeC_YNY_G_YNN** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **C_YNY_G_YNNEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **C_YNY_G_YNNQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │                         2. **Add **$$CreateC_YNY_G_YNN_DW** to/from list **$AllBidDownloadList****
   │          │                ➔ **If [true]:**
   │          │                   1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateB_NYY_D_NNY_DW**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory_DW/ecoID`
      - Set **Category** = `$IteratorAggregatedInventory_DW/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory_DW/Device_Id`
      - Set **BrandName** = `$IteratorAggregatedInventory_DW/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory_DW/Model_Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$IteratorBuyerCode/Code`
      - Set **B_NYY_D_NNY** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeB_NYY_D_NNY** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **B_NYY_D_NNYEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **B_NYY_D_NNYQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │                   2. **Add **$$CreateB_NYY_D_NNY_DW** to/from list **$AllBidDownloadList****
   │          │    ➔ **If [false]:**
   │          │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/Merged_Grade='A_YYY'`
   │          │          ➔ **If [true]:**
   │          │             1. **Update **$DeviceExistsCheck_DW**
      - Set **A_YYY** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeA_YYY** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **A_YYYEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **A_YYYQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │          ➔ **If [false]:**
   │          │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/Merged_Grade='B_NYY/D_NNY'`
   │          │                ➔ **If [false]:**
   │          │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/Merged_Grade='C_YNY/G_YNN'`
   │          │                      ➔ **If [false]:**
   │          │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory_DW/Merged_Grade='E_YYN'`
   │          │                            ➔ **If [false]:**
   │          │                               1. **Update **$DeviceExistsCheck_DW**
      - Set **F_NYN_H_NNN** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeF_NYN_H_NNN** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **F_NYN_H_NNNEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **F_NYN_H_NNNQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │                            ➔ **If [true]:**
   │          │                               1. **Update **$DeviceExistsCheck_DW**
      - Set **E_YYN** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeE_YYN** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **E_YYNEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **E_YYNQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │                      ➔ **If [true]:**
   │          │                         1. **Update **$DeviceExistsCheck_DW**
      - Set **C_YNY_G_YNN** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeC_YNY_G_YNN** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **C_YNY_G_YNNEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **C_YNY_G_YNNQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          │                ➔ **If [true]:**
   │          │                   1. **Update **$DeviceExistsCheck_DW**
      - Set **B_NYY_D_NNY** = `if $FindBidData_DW=empty then '$0.00' else '$'+toString($FindBidData_DW/BidAmount)`
      - Set **MAXofGradeB_NYY_D_NNY** = `'$'+toString($IteratorAggregatedInventory_DW/Data_Wipe_Target_Price)`
      - Set **B_NYY_D_NNYEstimatedQuantity** = `$IteratorAggregatedInventory_DW/Data_Wipe_Quantity`
      - Set **B_NYY_D_NNYQuantityCap** = `if $FindBidData_DW=empty or $FindBidData_DW/BidQuantity<0 then empty else $FindBidData_DW/BidQuantity`**
   │          └─ **End Loop**
   │       2. **Call Microflow **AuctionUI.SUB_AllBids_ExportExcel_PerBuyerCode** (Result: **$AllBidsZipTempList**)**
   └─ **End Loop**
16. **Delete**
17. 🔀 **DECISION:** `$FinalBuyerCode`
   ➔ **If [true]:**
      1. **Update **$AllBidDownload_ScreenHelper**
      - Set **R1Caption** = `if $SchedulingAuction/Round=1 then 'Click here for Bid Files' else $AllBidDownload_ScreenHelper/R1Caption`
      - Set **R2Caption** = `if $SchedulingAuction/Round=2 and $SchedulingAuction/IsActive then 'Click here for Bid Files' else $AllBidDownload_ScreenHelper/R2Caption`
      - Set **UpsellCaption** = `if $SchedulingAuction/Round=3 and $SchedulingAuction/IsActive then 'Click here for Bid Files' else $AllBidDownload_ScreenHelper/UpsellCaption`**
      2. **LogMessage**
      3. **Commit/Save **$AllBidDownload_ScreenHelper** to Database**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **LogMessage**
      2. **Commit/Save **$AllBidDownload_ScreenHelper** to Database**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.