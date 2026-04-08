# Microflow Detailed Specification: ACT_CreateBidDataHelper

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound**)**
2. **Retrieve related **BidData_Helper_BidRound** via Association from **$BidRound** (Result: **$BidData_HelperList_2**)**
3. **Delete**
4. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
5. **Retrieve related **Auction_Week** via Association from **$Week** (Result: **$Auction**)**
6. **Create Variable **$CurrentBidRound** = `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_SchedulingAuction/AuctionUI.SchedulingAuction/Round`**
7. **Call Microflow **AuctionUI.ACT_CreateBidDataHelper_AggregatedList** (Result: **$AgregatedInventory**)**
8. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week=$Week]` (Result: **$AllRounds_BidDataList**)**
9. **CreateList**
10. **Retrieve related **BidData_BidRound** via Association from **$BidRound** (Result: **$BidDataList_Old**)**
11. **CreateList**
12. **CreateList**
13. 🔀 **DECISION:** `$CurrentBidRound=1`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$CurrentBidRound=2`
         ➔ **If [true]:**
            1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound= $BidRound` (Result: **$CurrR2_BidDataList**)**
            2. **Add **$$CurrR2_BidDataList** to/from list **$CurrentRoundBidDataList****
            3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList**)**
            4. **Add **$$PrevR1_BidDataList** to/from list **$PreviousRoundBidDataList****
            5. 🔀 **DECISION:** `$CurrentBidRound=3`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                     ➔ **If [false]:**
                        1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                           │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                           │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                           │    ➔ **If [true]:**
                           │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                           │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                           │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                           │          ➔ **If [false]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                           │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                           │    ➔ **If [false]:**
                           │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                           │          ➔ **If [false]:**
                           │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                           │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                           │                ➔ **If [false]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                           │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                           │                ➔ **If [true]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                           └─ **End Loop**
                        2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                        3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                     ➔ **If [true]:**
                        1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                           │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                           │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                           │    ➔ **If [false]:**
                           │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                           │          ➔ **If [false]:**
                           │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                           │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                           │                ➔ **If [false]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                           │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │                ➔ **If [true]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │    ➔ **If [true]:**
                           │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                           │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                           │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │          ➔ **If [false]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                           │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                           └─ **End Loop**
                        2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                        3. 🏁 **END:** Return `$SortBidData_HelperList`
               ➔ **If [true]:**
                  1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound= $BidRound` (Result: **$CurrR3_BidDataList**)**
                  2. **Add **$$CurrR3_BidDataList** to/from list **$CurrentRoundBidDataList****
                  3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$PrevR2_BidDataList**)**
                  4. 🔀 **DECISION:** `$PrevR2_BidDataList=empty`
                     ➔ **If [false]:**
                        1. **Add **$$PrevR2_BidDataList** to/from list **$PreviousRoundBidDataList****
                        2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortBidData_HelperList`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList_**)**
                        2. **Add **$$PrevR1_BidDataList_** to/from list **$PreviousRoundBidDataList****
                        3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortBidData_HelperList`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$CurrentBidRound=3`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                     ➔ **If [false]:**
                        1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                           │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                           │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                           │    ➔ **If [true]:**
                           │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                           │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                           │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                           │          ➔ **If [false]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                           │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                           │    ➔ **If [false]:**
                           │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                           │          ➔ **If [false]:**
                           │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                           │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                           │                ➔ **If [false]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                           │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                           │                ➔ **If [true]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                           └─ **End Loop**
                        2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                        3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                     ➔ **If [true]:**
                        1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                           │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                           │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                           │    ➔ **If [false]:**
                           │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                           │          ➔ **If [false]:**
                           │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                           │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                           │                ➔ **If [false]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                           │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │                ➔ **If [true]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │    ➔ **If [true]:**
                           │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                           │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                           │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │          ➔ **If [false]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                           │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                           └─ **End Loop**
                        2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                        3. 🏁 **END:** Return `$SortBidData_HelperList`
               ➔ **If [true]:**
                  1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound= $BidRound` (Result: **$CurrR3_BidDataList**)**
                  2. **Add **$$CurrR3_BidDataList** to/from list **$CurrentRoundBidDataList****
                  3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$PrevR2_BidDataList**)**
                  4. 🔀 **DECISION:** `$PrevR2_BidDataList=empty`
                     ➔ **If [false]:**
                        1. **Add **$$PrevR2_BidDataList** to/from list **$PreviousRoundBidDataList****
                        2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortBidData_HelperList`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList_**)**
                        2. **Add **$$PrevR1_BidDataList_** to/from list **$PreviousRoundBidDataList****
                        3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortBidData_HelperList`
   ➔ **If [true]:**
      1. **Add **$$AllRounds_BidDataList** to/from list **$CurrentRoundBidDataList****
      2. 🔀 **DECISION:** `$CurrentBidRound=2`
         ➔ **If [true]:**
            1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound= $BidRound` (Result: **$CurrR2_BidDataList**)**
            2. **Add **$$CurrR2_BidDataList** to/from list **$CurrentRoundBidDataList****
            3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList**)**
            4. **Add **$$PrevR1_BidDataList** to/from list **$PreviousRoundBidDataList****
            5. 🔀 **DECISION:** `$CurrentBidRound=3`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                     ➔ **If [false]:**
                        1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                           │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                           │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                           │    ➔ **If [true]:**
                           │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                           │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                           │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                           │          ➔ **If [false]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                           │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                           │    ➔ **If [false]:**
                           │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                           │          ➔ **If [false]:**
                           │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                           │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                           │                ➔ **If [false]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                           │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                           │                ➔ **If [true]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                           └─ **End Loop**
                        2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                        3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                     ➔ **If [true]:**
                        1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                           │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                           │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                           │    ➔ **If [false]:**
                           │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                           │          ➔ **If [false]:**
                           │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                           │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                           │                ➔ **If [false]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                           │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │                ➔ **If [true]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │    ➔ **If [true]:**
                           │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                           │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                           │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │          ➔ **If [false]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                           │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                           └─ **End Loop**
                        2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                        3. 🏁 **END:** Return `$SortBidData_HelperList`
               ➔ **If [true]:**
                  1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound= $BidRound` (Result: **$CurrR3_BidDataList**)**
                  2. **Add **$$CurrR3_BidDataList** to/from list **$CurrentRoundBidDataList****
                  3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$PrevR2_BidDataList**)**
                  4. 🔀 **DECISION:** `$PrevR2_BidDataList=empty`
                     ➔ **If [false]:**
                        1. **Add **$$PrevR2_BidDataList** to/from list **$PreviousRoundBidDataList****
                        2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortBidData_HelperList`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList_**)**
                        2. **Add **$$PrevR1_BidDataList_** to/from list **$PreviousRoundBidDataList****
                        3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortBidData_HelperList`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$CurrentBidRound=3`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                     ➔ **If [false]:**
                        1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                           │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                           │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                           │    ➔ **If [true]:**
                           │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                           │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                           │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                           │          ➔ **If [false]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                           │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                           │    ➔ **If [false]:**
                           │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                           │          ➔ **If [false]:**
                           │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                           │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                           │                ➔ **If [false]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                           │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                           │                ➔ **If [true]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                           └─ **End Loop**
                        2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                        3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                     ➔ **If [true]:**
                        1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                           │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                           │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                           │    ➔ **If [false]:**
                           │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                           │          ➔ **If [false]:**
                           │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                           │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                           │                ➔ **If [false]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                           │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │                ➔ **If [true]:**
                           │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                           │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │    ➔ **If [true]:**
                           │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                           │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                           │          ➔ **If [true]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                           │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                           │          ➔ **If [false]:**
                           │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                           │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                           └─ **End Loop**
                        2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                        3. 🏁 **END:** Return `$SortBidData_HelperList`
               ➔ **If [true]:**
                  1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound= $BidRound` (Result: **$CurrR3_BidDataList**)**
                  2. **Add **$$CurrR3_BidDataList** to/from list **$CurrentRoundBidDataList****
                  3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$PrevR2_BidDataList**)**
                  4. 🔀 **DECISION:** `$PrevR2_BidDataList=empty`
                     ➔ **If [false]:**
                        1. **Add **$$PrevR2_BidDataList** to/from list **$PreviousRoundBidDataList****
                        2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortBidData_HelperList`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList_**)**
                        2. **Add **$$PrevR1_BidDataList_** to/from list **$PreviousRoundBidDataList****
                        3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_HelperList****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_HelperList****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **ecoGrade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_HelperList****
                                 └─ **End Loop**
                              2. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              3. 🏁 **END:** Return `$SortBidData_HelperList`

**Final Result:** This process concludes by returning a [List] value.