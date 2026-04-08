# Microflow Detailed Specification: SUB_CreateBidDataDownload_NoBids

### 📥 Inputs (Parameters)
- **$AggregatedInventoryList** (Type: AuctionUI.AggregatedInventory)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$AllBidDownloadList** (Type: AuctionUI.AllBidDownload)
- **$BidDataList** (Type: AuctionUI.BidData)
- **$NewAllBidsDoc** (Type: AuctionUI.AllBidsDoc)

### ⚙️ Execution Flow (Logic Steps)
1. 🔄 **LOOP:** For each **$IteratorAggregatedInventory** in **$AggregatedInventoryList**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventory/EcoId` (Result: **$DeviceExistsCheck**)**
   │ 2. **List Operation: **Find** on **$undefined** where `$IteratorAggregatedInventory` (Result: **$FindBidData**)**
   │ 3. 🔀 **DECISION:** `$DeviceExistsCheck=empty`
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='A_YYY'`
   │          ➔ **If [true]:**
   │             1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateA_YYY**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/EcoId`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/DeviceId`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **A_YYY** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeA_YYY** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **A_YYYEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **A_YYYQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc`**
   │             2. **Add **$$CreateA_YYY** to/from list **$AllBidDownloadList****
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='B_NYY/D_NNY'`
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='C_YNY/G_YNN'`
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='E_YYN'`
   │                            ➔ **If [true]:**
   │                               1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateE_YYN**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/EcoId`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/DeviceId`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **E_YYN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeE_YYN** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **E_YYNEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **E_YYNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc`**
   │                               2. **Add **$$CreateE_YYN** to/from list **$AllBidDownloadList****
   │                            ➔ **If [false]:**
   │                               1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateF_NYN_H_NNN**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/EcoId`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/DeviceId`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **F_NYN_H_NNN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeF_NYN_H_NNN** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **F_NYN_H_NNNEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **F_NYN_H_NNNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc`**
   │                               2. **Add **$$CreateF_NYN_H_NNN** to/from list **$AllBidDownloadList****
   │                      ➔ **If [true]:**
   │                         1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateC_YNY_G_YNN**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/EcoId`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/DeviceId`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **C_YNY_G_YNN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeC_YNY_G_YNN** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **C_YNY_G_YNNEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **C_YNY_G_YNNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc`**
   │                         2. **Add **$$CreateC_YNY_G_YNN** to/from list **$AllBidDownloadList****
   │                ➔ **If [true]:**
   │                   1. **Create **AuctionUI.AllBidDownload** (Result: **$CreateB_NYY_D_NNY**)
      - Set **ecoATMCode** = `$IteratorAggregatedInventory/EcoId`
      - Set **Category** = `$IteratorAggregatedInventory/Category`
      - Set **DeviceId** = `$IteratorAggregatedInventory/DeviceId`
      - Set **BrandName** = `$IteratorAggregatedInventory/Brand`
      - Set **PartNumber** = `''`
      - Set **PartName** = `$IteratorAggregatedInventory/Name`
      - Set **Added** = `''`
      - Set **BuyerCode** = `$BuyerCode/Code`
      - Set **B_NYY_D_NNY** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeB_NYY_D_NNY** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **B_NYY_D_NNYEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **B_NYY_D_NNYQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc`**
   │                   2. **Add **$$CreateB_NYY_D_NNY** to/from list **$AllBidDownloadList****
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='A_YYY'`
   │          ➔ **If [true]:**
   │             1. **Update **$DeviceExistsCheck**
      - Set **A_YYY** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeA_YYY** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **A_YYYEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **A_YYYQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │          ➔ **If [false]:**
   │             1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='B_NYY/D_NNY'`
   │                ➔ **If [false]:**
   │                   1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='C_YNY/G_YNN'`
   │                      ➔ **If [false]:**
   │                         1. 🔀 **DECISION:** `$IteratorAggregatedInventory/MergedGrade='E_YYN'`
   │                            ➔ **If [false]:**
   │                               1. **Update **$DeviceExistsCheck**
      - Set **F_NYN_H_NNN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeF_NYN_H_NNN** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **F_NYN_H_NNNEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **F_NYN_H_NNNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │                            ➔ **If [true]:**
   │                               1. **Update **$DeviceExistsCheck**
      - Set **E_YYN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeE_YYN** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **E_YYNEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **E_YYNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │                      ➔ **If [true]:**
   │                         1. **Update **$DeviceExistsCheck**
      - Set **C_YNY_G_YNN** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeC_YNY_G_YNN** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **C_YNY_G_YNNEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **C_YNY_G_YNNQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   │                ➔ **If [true]:**
   │                   1. **Update **$DeviceExistsCheck**
      - Set **B_NYY_D_NNY** = `if $FindBidData=empty then '$0.00' else '$'+toString($FindBidData/BidAmount)`
      - Set **MAXofGradeB_NYY_D_NNY** = `'$'+toString($IteratorAggregatedInventory/AvgTargetPrice)`
      - Set **B_NYY_D_NNYEstimatedQuantity** = `$IteratorAggregatedInventory/TotalQuantity`
      - Set **B_NYY_D_NNYQuantityCap** = `if $FindBidData=empty or $FindBidData/BidQuantity<0 then empty else $FindBidData/BidQuantity`**
   └─ **End Loop**
2. 🔄 **LOOP:** For each **$IteratorAllBidDownload** in **$AllBidDownloadList**
   │ 1. **Update **$IteratorAllBidDownload**
      - Set **AllBidDownload_AllBidsDoc** = `$NewAllBidsDoc`**
   └─ **End Loop**
3. **Commit/Save **$AllBidDownloadList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.