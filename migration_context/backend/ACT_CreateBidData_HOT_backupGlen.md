# Microflow Detailed Specification: ACT_CreateBidData_HOT_backupGlen

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_Caching.NP_BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound**)**
2. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
3. **DB Retrieve **EcoATM_Buyer.BidData** Filter: `[Code = $NP_BuyerCodeSelect_Helper/Code] [EcoATM_Buyer.BidData_BidRound = $BidRound]` (Result: **$Existing_BidDataList**)**
4. 🔀 **DECISION:** `$Existing_BidDataList = empty`
   ➔ **If [true]:**
      1. **Create Variable **$CurrentBidRound** = `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_SchedulingAuction/AuctionUI.SchedulingAuction/Round`**
      2. **Call Microflow **AuctionUI.ACT_CreateBidDataHelper_AggregatedList** (Result: **$AgregatedInventory**)**
      3. **DB Retrieve **EcoATM_Buyer.BidData** Filter: `[ EcoATM_Buyer.BidData_BuyerCode/AuctionUI.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code and EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week=$Week ]` (Result: **$AllRounds_BidDataList**)**
      4. **CreateList**
      5. **CreateList**
      6. **CreateList**
      7. 🔀 **DECISION:** `$CurrentBidRound=1`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$CurrentBidRound=2`
               ➔ **If [true]:**
                  1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound= $BidRound` (Result: **$CurrR2_BidDataList**)**
                  2. **Add **$$CurrR2_BidDataList** to/from list **$CurrentRoundBidDataList****
                  3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList**)**
                  4. **Add **$$PrevR1_BidDataList** to/from list **$PreviousRoundBidDataList****
                  5. 🔀 **DECISION:** `$CurrentBidRound=3`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound= $BidRound` (Result: **$CurrR3_BidDataList**)**
                        2. **Add **$$CurrR3_BidDataList** to/from list **$CurrentRoundBidDataList****
                        3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$PrevR2_BidDataList**)**
                        4. 🔀 **DECISION:** `$PrevR2_BidDataList=empty`
                           ➔ **If [false]:**
                              1. **Add **$$PrevR2_BidDataList** to/from list **$PreviousRoundBidDataList****
                              2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                       │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                       │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                       │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                       │    ➔ **If [false]:**
                                       │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                       │          ➔ **If [false]:**
                                       │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                       │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                       │                ➔ **If [false]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList_**)**
                              2. **Add **$$PrevR1_BidDataList_** to/from list **$PreviousRoundBidDataList****
                              3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                       │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                       │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                       │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                       │    ➔ **If [false]:**
                                       │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                       │          ➔ **If [false]:**
                                       │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                       │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                       │                ➔ **If [false]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              4. 🏁 **END:** Return `$SortBidData_HelperList`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$CurrentBidRound=3`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound= $BidRound` (Result: **$CurrR3_BidDataList**)**
                        2. **Add **$$CurrR3_BidDataList** to/from list **$CurrentRoundBidDataList****
                        3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$PrevR2_BidDataList**)**
                        4. 🔀 **DECISION:** `$PrevR2_BidDataList=empty`
                           ➔ **If [false]:**
                              1. **Add **$$PrevR2_BidDataList** to/from list **$PreviousRoundBidDataList****
                              2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                       │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                       │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                       │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                       │    ➔ **If [false]:**
                                       │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                       │          ➔ **If [false]:**
                                       │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                       │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                       │                ➔ **If [false]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList_**)**
                              2. **Add **$$PrevR1_BidDataList_** to/from list **$PreviousRoundBidDataList****
                              3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                       │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                       │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                       │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                       │    ➔ **If [false]:**
                                       │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                       │          ➔ **If [false]:**
                                       │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                       │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                       │                ➔ **If [false]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              4. 🏁 **END:** Return `$SortBidData_HelperList`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
         ➔ **If [true]:**
            1. **Add **$$AllRounds_BidDataList** to/from list **$CurrentRoundBidDataList****
            2. 🔀 **DECISION:** `$CurrentBidRound=2`
               ➔ **If [true]:**
                  1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound= $BidRound` (Result: **$CurrR2_BidDataList**)**
                  2. **Add **$$CurrR2_BidDataList** to/from list **$CurrentRoundBidDataList****
                  3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList**)**
                  4. **Add **$$PrevR1_BidDataList** to/from list **$PreviousRoundBidDataList****
                  5. 🔀 **DECISION:** `$CurrentBidRound=3`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound= $BidRound` (Result: **$CurrR3_BidDataList**)**
                        2. **Add **$$CurrR3_BidDataList** to/from list **$CurrentRoundBidDataList****
                        3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$PrevR2_BidDataList**)**
                        4. 🔀 **DECISION:** `$PrevR2_BidDataList=empty`
                           ➔ **If [false]:**
                              1. **Add **$$PrevR2_BidDataList** to/from list **$PreviousRoundBidDataList****
                              2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                       │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                       │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                       │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                       │    ➔ **If [false]:**
                                       │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                       │          ➔ **If [false]:**
                                       │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                       │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                       │                ➔ **If [false]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList_**)**
                              2. **Add **$$PrevR1_BidDataList_** to/from list **$PreviousRoundBidDataList****
                              3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                       │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                       │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                       │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                       │    ➔ **If [false]:**
                                       │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                       │          ➔ **If [false]:**
                                       │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                       │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                       │                ➔ **If [false]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              4. 🏁 **END:** Return `$SortBidData_HelperList`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$CurrentBidRound=3`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound= $BidRound` (Result: **$CurrR3_BidDataList**)**
                        2. **Add **$$CurrR3_BidDataList** to/from list **$CurrentRoundBidDataList****
                        3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=2` (Result: **$PrevR2_BidDataList**)**
                        4. 🔀 **DECISION:** `$PrevR2_BidDataList=empty`
                           ➔ **If [false]:**
                              1. **Add **$$PrevR2_BidDataList** to/from list **$PreviousRoundBidDataList****
                              2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                       │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                       │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                       │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                       │    ➔ **If [false]:**
                                       │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                       │          ➔ **If [false]:**
                                       │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                       │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                       │                ➔ **If [false]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                           ➔ **If [true]:**
                              1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList_**)**
                              2. **Add **$$PrevR1_BidDataList_** to/from list **$PreviousRoundBidDataList****
                              3. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                       │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                       │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                       │    ➔ **If [true]:**
                                       │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                       │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                       │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [false]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                       │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                       │    ➔ **If [false]:**
                                       │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                       │          ➔ **If [false]:**
                                       │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                       │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                       │                ➔ **If [false]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                       │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                       │                ➔ **If [true]:**
                                       │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       │          ➔ **If [true]:**
                                       │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                       │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                       └─ **End Loop**
                                    2. **Commit/Save **$BidData_List** to Database**
                                    3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                    4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_Caching.NP_BuyerCodeSelect_Helper_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
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
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              4. 🏁 **END:** Return `$SortBidData_HelperList`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`**
                                 │             2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `-1`
      - Set **PreviousRoundBidAmount** = `0`**
                                 │             2. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/DWAvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/DWTotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │                   2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **EcoATM_Buyer.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `$IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `-1`**
                                 │             2. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              4. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `$Existing_BidDataList`

**Final Result:** This process concludes by returning a [List] value.