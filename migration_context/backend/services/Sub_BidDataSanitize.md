# Microflow Detailed Specification: Sub_BidDataSanitize

### 📥 Inputs (Parameters)
- **$BidDataList** (Type: EcoATM_Buyer.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **EcoATM_Buyer.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
3. 🔀 **DECISION:** `$BuyerCodeSubmitConfig/ExecuteBidDataSanitizeFlow = true`
   ➔ **If [true]:**
      1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/Sanitized != true` (Result: **$UnSanitized**)**
      2. **List Operation: **Subtract** on **$undefined** (Result: **$Sanitized**)**
      3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/Submitted = true` (Result: **$NewBidDataList**)**
      4. **List Operation: **Subtract** on **$undefined** (Result: **$DeletionList**)**
      5. **List Operation: **Filter** on **$undefined** where `-1` (Result: **$BidDataListMinus1**)**
      6. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$BidDataListMinusEmpty**)**
      7. **List Operation: **Filter** on **$undefined** where `0` (Result: **$BidDataListMinusZero**)**
      8. **List Operation: **Union** on **$undefined** (Result: **$BidDataListMinusReady**)**
      9. **List Operation: **Subtract** on **$undefined** (Result: **$BidDataListSansMinus1New**)**
      10. 🔄 **LOOP:** For each **$IteratorBidDataNew** in **$BidDataListSansMinus1New**
         │ 1. **Update **$IteratorBidDataNew**
      - Set **SanitizedBidQuantity** = `if $IteratorBidDataNew/BidQuantity < 0 then 0 else $IteratorBidDataNew/BidQuantity`**
         └─ **End Loop**
      11. 🔄 **LOOP:** For each **$IteratorBidDataMinusReadyNew** in **$BidDataListMinusReady**
         │ 1. 🔀 **DECISION:** `$IteratorBidDataMinusReadyNew/BidAmount != empty or $IteratorBidDataMinusReadyNew/BidAmount != -1`
         │    ➔ **If [true]:**
         │       1. **Update **$IteratorBidDataMinusReadyNew**
      - Set **SanitizedBidQuantity** = `if ($IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/TotalQuantity != empty and ($IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType != AuctionUI.enum_BuyerCodeType.Data_Wipe or $IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType != AuctionUI.enum_BuyerCodeType.Data_Wipe) ) then $IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/TotalQuantity else if (($IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/TotalQuantity != empty or $IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/DWTotalQuantity != empty) and ($IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe or $IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/AuctionUI.BuyerCode/BuyerCodeType = AuctionUI.enum_BuyerCodeType.Data_Wipe) ) then $IteratorBidDataMinusReadyNew/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/DWTotalQuantity else 0`**
         │       2. **Add **$$IteratorBidDataMinusReadyNew** to/from list **$BidDataListSansMinus1New****
         │    ➔ **If [false]:**
         └─ **End Loop**
      12. **CreateList**
      13. **CreateList**
      14. **CreateList**
      15. 🔄 **LOOP:** For each **$CurrentBidData** in **$BidDataListSansMinus1New**
         │ 1. **Create **AuctionUI.BidDataDuplicateHelper** (Result: **$NewBidDataDuplicateHelper**)
      - Set **EcoID** = `$CurrentBidData/EcoID`
      - Set **Merged_Grade** = `$CurrentBidData/Merged_Grade`
      - Set **Round** = `$CurrentBidData/EcoATM_Buyer.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round`
      - Set **Code** = `$CurrentBidData/EcoATM_Buyer.BidData_BuyerCode/AuctionUI.BuyerCode/Code`**
         │ 2. **List Operation: **Filter** on **$undefined** where `$NewBidDataDuplicateHelper/EcoID` (Result: **$NewBidDataDuplicateHelperList**)**
         │ 3. **List Operation: **Filter** on **$undefined** where `$NewBidDataDuplicateHelper/Merged_Grade` (Result: **$NewBidDataDuplicateHelperList_1**)**
         │ 4. **List Operation: **Filter** on **$undefined** where `$NewBidDataDuplicateHelper/Round` (Result: **$NewBidDataDuplicateHelperList_2**)**
         │ 5. **List Operation: **Filter** on **$undefined** where `$NewBidDataDuplicateHelper/Code` (Result: **$NewBidDataDuplicateHelperList_3**)**
         │ 6. 🔀 **DECISION:** `$NewBidDataDuplicateHelperList_3 = empty`
         │    ➔ **If [false]:**
         │       1. **Add **$$CurrentBidData** to/from list **$DeletionList****
         │    ➔ **If [true]:**
         │       1. 🔀 **DECISION:** `$CurrentBidData/BidAmount != empty and $CurrentBidData/BidAmount > 0`
         │          ➔ **If [true]:**
         │             1. **Add **$$NewBidDataDuplicateHelper** to/from list **$BidDataDuplicateHelperList****
         │             2. **Update **$CurrentBidData**
      - Set **Payout** = `if $CurrentBidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AvgPayout != empty then (($CurrentBidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AvgPayout div $CurrentBidData/EcoATM_Buyer.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/TotalQuantity) * $CurrentBidData/SanitizedBidQuantity) else 0`
      - Set **Sanitized** = `true`**
         │             3. **Add **$$CurrentBidData** to/from list **$UniqueBidDataList****
         │          ➔ **If [false]:**
         │             1. **Update **$CurrentBidData**
      - Set **Sanitized** = `false`**
         │             2. **Add **$$CurrentBidData** to/from list **$IgnoreList****
         └─ **End Loop**
      16. **AggregateList**
      17. **Create Variable **$Offset** = `0`**
      18. **Create Variable **$Amount** = `@EcoATM_Direct_Sharepoint.CONST_EndOfRoundAllBidDataDownloadDeleteAmount`**
      19. **Create Variable **$ProcessedCount** = `0`**
      20. **JavaCallAction**
      21. **List Operation: **ListRange** on **$undefined** (Result: **$DeletionListSegment**)**
      22. **AggregateList**
      23. **Delete**
      24. **Update Variable **$ProcessedCount** = `$ProcessedCount + $RetrievedCount`**
      25. **JavaCallAction**
      26. **Call Microflow **Custom_Logging.SUB_Log_Info****
      27. **Update Variable **$RetrievedCount** = `0`**
      28. 🔀 **DECISION:** `$ProcessedCount >= $DeletionCount`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$DeletionList = empty`
               ➔ **If [true]:**
                  1. **Delete**
                  2. **List Operation: **Union** on **$undefined** (Result: **$FinalSanitized**)**
                  3. **Commit/Save **$FinalSanitized** to Database**
                  4. **AggregateList**
                  5. **Create Variable **$EndTime** = `[%CurrentDateTime%]`**
                  6. **Commit/Save **$IgnoreList** to Database**
                  7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  8. 🏁 **END:** Return `$FinalSanitized`
               ➔ **If [false]:**
                  1. **JavaCallAction**
                  2. **AggregateList**
                  3. **Delete**
                  4. **JavaCallAction**
                  5. **Call Microflow **Custom_Logging.SUB_Log_Info****
                  6. **Delete**
                  7. **List Operation: **Union** on **$undefined** (Result: **$FinalSanitized**)**
                  8. **Commit/Save **$FinalSanitized** to Database**
                  9. **AggregateList**
                  10. **Create Variable **$EndTime** = `[%CurrentDateTime%]`**
                  11. **Commit/Save **$IgnoreList** to Database**
                  12. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  13. 🏁 **END:** Return `$FinalSanitized`
         ➔ **If [false]:**
               *(Merging with existing path logic)*
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.