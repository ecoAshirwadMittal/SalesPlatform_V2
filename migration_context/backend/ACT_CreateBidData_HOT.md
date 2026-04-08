# Microflow Detailed Specification: ACT_CreateBidData_HOT

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$BidderRouterHelper** (Type: AuctionUI.BidderRouterHelper)
- **$Parent_NPBuyerCodeSelectHelper** (Type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **NP_BuyerCodeSelect_Helper_BidRound** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BidRound**)**
3. **Create Variable **$BidQuantityDefault** = `empty`**
4. **Retrieve related **BidRound_BuyerCode** via Association from **$BidRound** (Result: **$BuyerCode_2**)**
5. **Retrieve related **BidRound_SchedulingAuction** via Association from **$BidRound** (Result: **$SchedulingAuction**)**
6. 🔀 **DECISION:** `$BidRound != empty`
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. **Show Message (Error): `ACT_CreateBidData_HOT Bidround is empty.`**
      3. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. **Create Variable **$CurrentBidRound** = `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_SchedulingAuction/AuctionUI.SchedulingAuction/Round`**
      2. **Retrieve related **NP_BuyerCodeSelect_Helper_BuyerCode** via Association from **$NP_BuyerCodeSelect_Helper** (Result: **$BuyerCode**)**
      3. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
      4. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound]` (Result: **$Existing_BidDataList**)**
      5. 🔀 **DECISION:** `$Existing_BidDataList = empty`
         ➔ **If [true]:**
            1. **Call Microflow **AuctionUI.ACT_BidDataDoc_GetOrCreate** (Result: **$BidDataDoc**)**
            2. **DB Retrieve **AuctionUI.BidData** Filter: `[ AuctionUI.BidData_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week=$Week ]` (Result: **$AllRounds_BidDataList**)**
            3. **CreateList**
            4. **CreateList**
            5. **CreateList**
            6. **Call Microflow **AuctionUI.ACT_CreateBidDataHelper_AggregatedList** (Result: **$AgregatedInventory**)**
            7. 🔀 **DECISION:** `$CurrentBidRound=1`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$CurrentBidRound=2`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound= $BidRound` (Result: **$CurrR2_BidDataList**)**
                        2. 🔀 **DECISION:** `$CurrR2_BidDataList != empty`
                           ➔ **If [false]:**
                              1. **Call Microflow **EcoATM_BuyerManagement.SUB_ListSpecialTreatmentBuyerBidData** (Result: **$BidDataList_Special**)**
                              2. 🔀 **DECISION:** `$BidDataList_Special = empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$AgregatedInventory != empty`
                                       ➔ **If [true]:**
                                          1. **Add **$$CurrR2_BidDataList** to/from list **$CurrentRoundBidDataList****
                                          2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList**)**
                                          3. **Add **$$PrevR1_BidDataList** to/from list **$PreviousRoundBidDataList****
                                          4. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
                                          5. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                                             ➔ **If [true]:**
                                                1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                                   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                                   │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory_DW/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory_DW/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig_1**)**
                                                   │ 3. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                                   │    ➔ **If [false]:**
                                                   │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                                   │          ➔ **If [false]:**
                                                   │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                                   │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                                   │                ➔ **If [false]:**
                                                   │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundFindBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundFindBidData_DW/DisplayRound2BidRank`**
                                                   │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                                   │                ➔ **If [true]:**
                                                   │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                                   │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                                   │          ➔ **If [true]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                                   │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                                   │    ➔ **If [true]:**
                                                   │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                                   │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                                   │          ➔ **If [true]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundBidData_DW/DisplayRound2BidRank`**
                                                   │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                                   │          ➔ **If [false]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **BidAmount** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                                   │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                                   └─ **End Loop**
                                                2. **Commit/Save **$BidData_List** to Database**
                                                3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                                4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                5. 🏁 **END:** Return `$SortBidData_HelperList`
                                             ➔ **If [false]:**
                                                1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                                   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                                   │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig**)**
                                                   │ 3. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                                   │    ➔ **If [true]:**
                                                   │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                                   │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                                   │          ➔ **If [true]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │             3. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                                   │          ➔ **If [false]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │             3. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                                   │    ➔ **If [false]:**
                                                   │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                                   │          ➔ **If [false]:**
                                                   │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                                   │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                                   │                ➔ **If [false]:**
                                                   │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │                   3. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                                   │                ➔ **If [true]:**
                                                   │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │                   3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                                   │          ➔ **If [true]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │             3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                                   └─ **End Loop**
                                                2. **Commit/Save **$BidData_List** to Database**
                                                3. **Commit/Save **$AgregatedInventory** to Database**
                                                4. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                                5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                6. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                                       ➔ **If [false]:**
                                          1. **Call Microflow **EcoATM_BuyerManagement.ACT_GetSubmittedBidRounds** (Result: **$BidRouterHelper**)**
                                          2. **Close current page/popup**
                                          3. **Maps to Page: **EcoATM_BuyerManagement.BidDownloadOnBuyerCodeSelect****
                                          4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          5. 🏁 **END:** Return `empty`
                                 ➔ **If [false]:**
                                    1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                    2. 🏁 **END:** Return `$BidDataList_Special`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$AgregatedInventory != empty`
                                 ➔ **If [true]:**
                                    1. **Add **$$CurrR2_BidDataList** to/from list **$CurrentRoundBidDataList****
                                    2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList**)**
                                    3. **Add **$$PrevR1_BidDataList** to/from list **$PreviousRoundBidDataList****
                                    4. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
                                    5. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                                       ➔ **If [true]:**
                                          1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                             │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                             │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory_DW/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory_DW/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig_1**)**
                                             │ 3. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                             │    ➔ **If [false]:**
                                             │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                             │          ➔ **If [false]:**
                                             │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                             │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                             │                ➔ **If [false]:**
                                             │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundFindBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundFindBidData_DW/DisplayRound2BidRank`**
                                             │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                             │                ➔ **If [true]:**
                                             │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                             │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                             │          ➔ **If [true]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                             │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                             │    ➔ **If [true]:**
                                             │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                             │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                             │          ➔ **If [true]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundBidData_DW/DisplayRound2BidRank`**
                                             │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                             │          ➔ **If [false]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **BidAmount** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                             │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                             └─ **End Loop**
                                          2. **Commit/Save **$BidData_List** to Database**
                                          3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                          4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          5. 🏁 **END:** Return `$SortBidData_HelperList`
                                       ➔ **If [false]:**
                                          1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                             │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                             │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig**)**
                                             │ 3. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                             │    ➔ **If [true]:**
                                             │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                             │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                             │          ➔ **If [true]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │             3. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                             │          ➔ **If [false]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │             3. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                             │    ➔ **If [false]:**
                                             │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                             │          ➔ **If [false]:**
                                             │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                             │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                             │                ➔ **If [false]:**
                                             │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │                   3. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                             │                ➔ **If [true]:**
                                             │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │                   3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                             │          ➔ **If [true]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │             3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                             └─ **End Loop**
                                          2. **Commit/Save **$BidData_List** to Database**
                                          3. **Commit/Save **$AgregatedInventory** to Database**
                                          4. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                          5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          6. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. **Call Microflow **EcoATM_BuyerManagement.ACT_GetSubmittedBidRounds** (Result: **$BidRouterHelper**)**
                                    2. **Close current page/popup**
                                    3. **Maps to Page: **EcoATM_BuyerManagement.BidDownloadOnBuyerCodeSelect****
                                    4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                    5. 🏁 **END:** Return `empty`
                     ➔ **If [false]:**
                        1. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
                        2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory_DW/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory_DW/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig_1**)**
                                 │ 3. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundFindBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundFindBidData_DW/DisplayRound2BidRank`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundBidData_DW/DisplayRound2BidRank`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **BidAmount** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                              5. 🏁 **END:** Return `$SortBidData_HelperList`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig**)**
                                 │ 3. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │             3. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │             3. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │                   3. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │                   3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │             3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **Commit/Save **$AgregatedInventory** to Database**
                              4. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                              6. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
               ➔ **If [true]:**
                  1. **Add **$$AllRounds_BidDataList** to/from list **$CurrentRoundBidDataList****
                  2. 🔀 **DECISION:** `$CurrentBidRound=2`
                     ➔ **If [true]:**
                        1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound= $BidRound` (Result: **$CurrR2_BidDataList**)**
                        2. 🔀 **DECISION:** `$CurrR2_BidDataList != empty`
                           ➔ **If [false]:**
                              1. **Call Microflow **EcoATM_BuyerManagement.SUB_ListSpecialTreatmentBuyerBidData** (Result: **$BidDataList_Special**)**
                              2. 🔀 **DECISION:** `$BidDataList_Special = empty`
                                 ➔ **If [true]:**
                                    1. 🔀 **DECISION:** `$AgregatedInventory != empty`
                                       ➔ **If [true]:**
                                          1. **Add **$$CurrR2_BidDataList** to/from list **$CurrentRoundBidDataList****
                                          2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList**)**
                                          3. **Add **$$PrevR1_BidDataList** to/from list **$PreviousRoundBidDataList****
                                          4. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
                                          5. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                                             ➔ **If [true]:**
                                                1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                                   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                                   │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory_DW/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory_DW/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig_1**)**
                                                   │ 3. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                                   │    ➔ **If [false]:**
                                                   │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                                   │          ➔ **If [false]:**
                                                   │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                                   │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                                   │                ➔ **If [false]:**
                                                   │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundFindBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundFindBidData_DW/DisplayRound2BidRank`**
                                                   │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                                   │                ➔ **If [true]:**
                                                   │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                                   │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                                   │          ➔ **If [true]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                                   │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                                   │    ➔ **If [true]:**
                                                   │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                                   │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                                   │          ➔ **If [true]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundBidData_DW/DisplayRound2BidRank`**
                                                   │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                                   │          ➔ **If [false]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **BidAmount** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                                   │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                                   └─ **End Loop**
                                                2. **Commit/Save **$BidData_List** to Database**
                                                3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                                4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                5. 🏁 **END:** Return `$SortBidData_HelperList`
                                             ➔ **If [false]:**
                                                1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                                   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                                   │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig**)**
                                                   │ 3. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                                   │    ➔ **If [true]:**
                                                   │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                                   │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                                   │          ➔ **If [true]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │             3. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                                   │          ➔ **If [false]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │             3. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                                   │    ➔ **If [false]:**
                                                   │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                                   │          ➔ **If [false]:**
                                                   │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                                   │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                                   │                ➔ **If [false]:**
                                                   │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │                   3. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                                   │                ➔ **If [true]:**
                                                   │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │                   3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                                   │          ➔ **If [true]:**
                                                   │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                                   │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                                   │             3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                                   └─ **End Loop**
                                                2. **Commit/Save **$BidData_List** to Database**
                                                3. **Commit/Save **$AgregatedInventory** to Database**
                                                4. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                                5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                                6. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                                       ➔ **If [false]:**
                                          1. **Call Microflow **EcoATM_BuyerManagement.ACT_GetSubmittedBidRounds** (Result: **$BidRouterHelper**)**
                                          2. **Close current page/popup**
                                          3. **Maps to Page: **EcoATM_BuyerManagement.BidDownloadOnBuyerCodeSelect****
                                          4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          5. 🏁 **END:** Return `empty`
                                 ➔ **If [false]:**
                                    1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                    2. 🏁 **END:** Return `$BidDataList_Special`
                           ➔ **If [true]:**
                              1. 🔀 **DECISION:** `$AgregatedInventory != empty`
                                 ➔ **If [true]:**
                                    1. **Add **$$CurrR2_BidDataList** to/from list **$CurrentRoundBidDataList****
                                    2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round=1` (Result: **$PrevR1_BidDataList**)**
                                    3. **Add **$$PrevR1_BidDataList** to/from list **$PreviousRoundBidDataList****
                                    4. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
                                    5. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                                       ➔ **If [true]:**
                                          1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                             │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                             │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory_DW/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory_DW/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig_1**)**
                                             │ 3. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                             │    ➔ **If [false]:**
                                             │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                             │          ➔ **If [false]:**
                                             │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                             │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                             │                ➔ **If [false]:**
                                             │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundFindBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundFindBidData_DW/DisplayRound2BidRank`**
                                             │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                             │                ➔ **If [true]:**
                                             │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                             │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                             │          ➔ **If [true]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                             │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                             │    ➔ **If [true]:**
                                             │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                             │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                             │          ➔ **If [true]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundBidData_DW/DisplayRound2BidRank`**
                                             │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                             │          ➔ **If [false]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **BidAmount** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                             │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                             └─ **End Loop**
                                          2. **Commit/Save **$BidData_List** to Database**
                                          3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                                          4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          5. 🏁 **END:** Return `$SortBidData_HelperList`
                                       ➔ **If [false]:**
                                          1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                             │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                             │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig**)**
                                             │ 3. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                             │    ➔ **If [true]:**
                                             │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                             │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                             │          ➔ **If [true]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │             3. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                             │          ➔ **If [false]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │             3. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                             │    ➔ **If [false]:**
                                             │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                             │          ➔ **If [false]:**
                                             │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                             │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                             │                ➔ **If [false]:**
                                             │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │                   3. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                             │                ➔ **If [true]:**
                                             │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │                   3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                             │          ➔ **If [true]:**
                                             │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                             │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                             │             3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                             └─ **End Loop**
                                          2. **Commit/Save **$BidData_List** to Database**
                                          3. **Commit/Save **$AgregatedInventory** to Database**
                                          4. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                                          5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                          6. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
                                 ➔ **If [false]:**
                                    1. **Call Microflow **EcoATM_BuyerManagement.ACT_GetSubmittedBidRounds** (Result: **$BidRouterHelper**)**
                                    2. **Close current page/popup**
                                    3. **Maps to Page: **EcoATM_BuyerManagement.BidDownloadOnBuyerCodeSelect****
                                    4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                                    5. 🏁 **END:** Return `empty`
                     ➔ **If [false]:**
                        1. **DB Retrieve **AuctionUI.BidDataTotalQuantityConfig**  (Result: **$BidDataTotalQuantityConfigList**)**
                        2. 🔀 **DECISION:** `$NP_BuyerCodeSelect_Helper/EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper_BuyerCode/EcoATM_BuyerManagement.BuyerCode/BuyerCodeType= AuctionUI.enum_BuyerCodeType.Data_Wipe`
                           ➔ **If [true]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DW** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$ExistingBidData_DW**)**
                                 │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory_DW/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory_DW/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig_1**)**
                                 │ 3. 🔀 **DECISION:** `$ExistingBidData_DW!=empty`
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundFindBidData_DW**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData_DW=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData_DW/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData_DW/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundFindBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundFindBidData_DW/DisplayRound2BidRank`**
                                 │                   2. **Add **$$PreviousRoundBidData_Helper_DW** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                 │                   2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                 │             2. **Add **$$NewBidData_Helper_DW** to/from list **$BidData_List****
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory_DW/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory_DW/MergedGrade` (Result: **$PreviousRoundBidData_DW**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData_DW!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData_DW/BidQuantity`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData_DW/BidAmount`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$PreviousRoundBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$PreviousRoundBidData_DW/DisplayRound2BidRank`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_DW_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory_DW/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory_DW`
      - Set **BidQuantity** = `$ExistingBidData_DW/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData_DW/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory_DW/DWAvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory_DW/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **BidAmount** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`
      - Set **BuyerCodeType** = `'Data_Wipe'`
      - Set **Data_Wipe_Quantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig_1!=empty then $IteratorAgregatedInventory_DW/DWTotalQuantity+$MatchingBidDataTotalQuantityConfig_1/DataWipeQuantity else $IteratorAgregatedInventory_DW/DWTotalQuantity`
      - Set **Round2BidRank** = `$ExistingBidData_DW/Round2BidRank`
      - Set **DisplayRound2BidRank** = `$ExistingBidData_DW/DisplayRound2BidRank`**
                                 │             2. **Add **$$UpdateBidData_Helper_DW_2** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortBidData_HelperList**)**
                              4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                              5. 🏁 **END:** Return `$SortBidData_HelperList`
                           ➔ **If [false]:**
                              1. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory**
                                 │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$ExistingBidData**)**
                                 │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID=$IteratorAgregatedInventory/EcoId and toString($currentObject/Grade)=$IteratorAgregatedInventory/MergedGrade` (Result: **$MatchingBidDataTotalQuantityConfig**)**
                                 │ 3. 🔀 **DECISION:** `$ExistingBidData!=empty`
                                 │    ➔ **If [true]:**
                                 │       1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundBidData**)**
                                 │       2. 🔀 **DECISION:** `$PreviousRoundBidData!=empty`
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │             3. **Add **$$UpdateBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [false]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$UpdateBidData_Helper_2**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │             3. **Add **$$UpdateBidData_Helper_2** to/from list **$BidData_List****
                                 │    ➔ **If [false]:**
                                 │       1. 🔀 **DECISION:** `$PreviousRoundBidDataList=empty`
                                 │          ➔ **If [false]:**
                                 │             1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $IteratorAgregatedInventory/EcoId and $currentObject/Merged_Grade = $IteratorAgregatedInventory/MergedGrade` (Result: **$PreviousRoundFindBidData**)**
                                 │             2. 🔀 **DECISION:** `$PreviousRoundFindBidData=empty`
                                 │                ➔ **If [false]:**
                                 │                   1. **Create **AuctionUI.BidData** (Result: **$PreviousRoundBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **BidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `$PreviousRoundFindBidData/BidAmount`
      - Set **PreviousRoundBidQuantity** = `$PreviousRoundFindBidData/BidQuantity`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │                   3. **Add **$$PreviousRoundBidData_Helper** to/from list **$BidData_List****
                                 │                ➔ **If [true]:**
                                 │                   1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │                   2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │                   3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 │          ➔ **If [true]:**
                                 │             1. **Create **AuctionUI.BidData** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/EcoId`
      - Set **BidData_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `$BidQuantityDefault`
      - Set **Merged_Grade** = `$IteratorAgregatedInventory/MergedGrade`
      - Set **Code** = `$NP_BuyerCodeSelect_Helper/Code`
      - Set **BidData_BidRound** = `$BidRound`
      - Set **CompanyName** = `$NP_BuyerCodeSelect_Helper/CompanyName`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/AvgTargetPrice`
      - Set **MaximumQuantity** = `if $MatchingBidDataTotalQuantityConfig!=empty then $IteratorAgregatedInventory/TotalQuantity+ $MatchingBidDataTotalQuantityConfig/NonDWQuantity+ $MatchingBidDataTotalQuantityConfig/DataWipeQuantity else $IteratorAgregatedInventory/TotalQuantity`
      - Set **BidRound** = `$CurrentBidRound`
      - Set **PreviousRoundBidAmount** = `0`
      - Set **PreviousRoundBidQuantity** = `$BidQuantityDefault`
      - Set **BidData_BuyerCode** = `$BuyerCode`
      - Set **BidData_BidDataDoc** = `$BidDataDoc`**
                                 │             2. **Update **$IteratorAgregatedInventory**
      - Set **isTotalQuantityModified** = `if $MatchingBidDataTotalQuantityConfig!=empty then true else false`**
                                 │             3. **Add **$$NewBidData_Helper** to/from list **$BidData_List****
                                 └─ **End Loop**
                              2. **Commit/Save **$BidData_List** to Database**
                              3. **Commit/Save **$AgregatedInventory** to Database**
                              4. **List Operation: **Sort** on **$undefined** sorted by: BidAmount (Descending), TargetPrice (Descending) (Result: **$SortedSalesBidData_HelperList**)**
                              5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                              6. 🏁 **END:** Return `$SortedSalesBidData_HelperList`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. 🏁 **END:** Return `$Existing_BidDataList`

**Final Result:** This process concludes by returning a [List] value.