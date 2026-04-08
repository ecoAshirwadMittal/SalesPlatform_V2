# Microflow Detailed Specification: SUB_BidData_Transform

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$ExcelIMport_BidData** (Type: Custom_Excel_Import.BidDataExport)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$ImportFile** (Type: Custom_Excel_Import.ImportFile)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **Create Variable **$Failed** = `false`**
3. **Retrieve related **BidRound_SchedulingAuction** via Association from **$BidRound** (Result: **$ScheduleAuction**)**
4. **Retrieve related **SchedulingAuction_Auction** via Association from **$ScheduleAuction** (Result: **$Auction**)**
5. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[AuctionUI.SchedulingAuction_Auction = $Auction and Round = $ScheduleAuction/Round -1]` (Result: **$ScheduleAuction_PriorRound**)**
6. **Create Variable **$isSPTBuyer** = `false`**
7. 🔀 **DECISION:** `$ScheduleAuction_PriorRound != empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_BuyerManagement.SUB_IsSpecialTreatmentBuyer** (Result: **$isSpecialTreatmentBuyer**)**
      2. **Update Variable **$isSPTBuyer** = `$isSpecialTreatmentBuyer`**
      3. **Update Variable **$isSPTBuyer** = `false`**
      4. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
      5. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound]` (Result: **$Existing_BidDataList**)**
      6. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction = $ScheduleAuction_PriorRound and AuctionUI.BidData_BuyerCode = $BidRound/AuctionUI.BidRound_BuyerCode]` (Result: **$Existing_BidDataList_PriorRound**)**
      7. **DB Retrieve **AuctionUI.BidData** Filter: `[Code = $NP_BuyerCodeSelect_Helper/Code] [AuctionUI.BidData_BidRound = $BidRound]` (Result: **$Existing_BidDataList_1**)**
      8. **CreateList**
      9. 🔄 **LOOP:** For each **$IteratorImportedBidData** in **$ExcelIMport_BidData**
         │ 1. **Call Microflow **EcoATM_BidData.SUB_BidData_ParseCapQty** (Result: **$ParsedCapQty**)**
         │ 2. 🔀 **DECISION:** `$ParsedCapQty != -999`
         │    ➔ **If [true]:**
         │       1. **Call Microflow **EcoATM_BidData.SUB_BidData_ParseAvailQty** (Result: **$ParsedAvailableQtyInteger**)**
         │       2. 🔀 **DECISION:** `$ParsedAvailableQtyInteger != -1`
         │          ➔ **If [true]:**
         │             1. **Call Microflow **EcoATM_BidData.SUB_BidData_ParseProductID** (Result: **$ParsedProductID**)**
         │             2. 🔀 **DECISION:** `$ParsedProductID != -1`
         │                ➔ **If [true]:**
         │                   1. **Create Variable **$ParsedPrice** = `parseDecimal($IteratorImportedBidData/Price)`**
         │                   2. 🔀 **DECISION:** `$ParsedPrice != -1`
         │                      ➔ **If [true]:**
         │                         1. 🔀 **DECISION:** `not(contains($IteratorImportedBidData/Price,'$'))`
         │                            ➔ **If [true]:**
         │                               1. **Update **$IteratorImportedBidData**
      - Set **Price** = `trim($IteratorImportedBidData/Price)`**
         │                               2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $ParsedProductID and $currentObject/Merged_Grade = $IteratorImportedBidData/Grade` (Result: **$LookupBidDataItem**)**
         │                               3. 🔀 **DECISION:** `$LookupBidDataItem != empty`
         │                                  ➔ **If [true]:**
         │                                     1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $ParsedProductID and $currentObject/Merged_Grade = $IteratorImportedBidData/Grade and $currentObject/BidAmount > 0` (Result: **$LookupBidDataItem_PriorRound**)**
         │                                     2. 🔀 **DECISION:** `$ScheduleAuction/Round = 1 or $isSPTBuyer or $LookupBidDataItem_PriorRound = empty`
         │                                        ➔ **If [true]:**
         │                                           1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                           2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                        ➔ **If [false]:**
         │                                           1. 🔀 **DECISION:** `$ScheduleAuction/Round = 2`
         │                                              ➔ **If [false]:**
         │                                                 1. 🔀 **DECISION:** `$ScheduleAuction/Round = 3`
         │                                                    ➔ **If [false]:**
         │                                                    ➔ **If [true]:**
         │                                                       1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidAmount =0 or $LookupBidDataItem/PreviousRoundBidAmount <= parseDecimal($IteratorImportedBidData/Price)`
         │                                                          ➔ **If [true]:**
         │                                                             1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $LookupBidDataItem/PreviousRoundBidQuantity = empty or $LookupBidDataItem/PreviousRoundBidQuantity = -1`
         │                                                                ➔ **If [false]:**
         │                                                                   1. 🔀 **DECISION:** `$ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                      ➔ **If [false]:**
         │                                                                         1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty >= $LookupBidDataItem/PreviousRoundBidQuantity`
         │                                                                            ➔ **If [false]:**
         │                                                                            ➔ **If [true]:**
         │                                                                               1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                               2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [true]:**
         │                                                                   1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                      ➔ **If [false]:**
         │                                                          ➔ **If [false]:**
         │                                              ➔ **If [true]:**
         │                                                 1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidAmount =0 or $LookupBidDataItem/PreviousRoundBidAmount <= parseDecimal($IteratorImportedBidData/Price)`
         │                                                    ➔ **If [true]:**
         │                                                       1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $LookupBidDataItem/PreviousRoundBidQuantity = empty or $LookupBidDataItem/PreviousRoundBidQuantity = -1`
         │                                                          ➔ **If [false]:**
         │                                                             1. 🔀 **DECISION:** `$ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                ➔ **If [true]:**
         │                                                                   1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                   2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [false]:**
         │                                                                   1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty >= $LookupBidDataItem/PreviousRoundBidQuantity`
         │                                                                      ➔ **If [false]:**
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                          ➔ **If [true]:**
         │                                                             1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                ➔ **If [true]:**
         │                                                                   1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                   2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [false]:**
         │                                                    ➔ **If [false]:**
         │                                  ➔ **If [false]:**
         │                            ➔ **If [false]:**
         │                               1. **Update **$IteratorImportedBidData**
      - Set **Price** = `replaceAll($IteratorImportedBidData/Price,'$','')`**
         │                               2. **Update **$IteratorImportedBidData**
      - Set **Price** = `trim($IteratorImportedBidData/Price)`**
         │                               3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $ParsedProductID and $currentObject/Merged_Grade = $IteratorImportedBidData/Grade` (Result: **$LookupBidDataItem**)**
         │                               4. 🔀 **DECISION:** `$LookupBidDataItem != empty`
         │                                  ➔ **If [true]:**
         │                                     1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $ParsedProductID and $currentObject/Merged_Grade = $IteratorImportedBidData/Grade and $currentObject/BidAmount > 0` (Result: **$LookupBidDataItem_PriorRound**)**
         │                                     2. 🔀 **DECISION:** `$ScheduleAuction/Round = 1 or $isSPTBuyer or $LookupBidDataItem_PriorRound = empty`
         │                                        ➔ **If [true]:**
         │                                           1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                           2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                        ➔ **If [false]:**
         │                                           1. 🔀 **DECISION:** `$ScheduleAuction/Round = 2`
         │                                              ➔ **If [false]:**
         │                                                 1. 🔀 **DECISION:** `$ScheduleAuction/Round = 3`
         │                                                    ➔ **If [false]:**
         │                                                    ➔ **If [true]:**
         │                                                       1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidAmount =0 or $LookupBidDataItem/PreviousRoundBidAmount <= parseDecimal($IteratorImportedBidData/Price)`
         │                                                          ➔ **If [true]:**
         │                                                             1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $LookupBidDataItem/PreviousRoundBidQuantity = empty or $LookupBidDataItem/PreviousRoundBidQuantity = -1`
         │                                                                ➔ **If [false]:**
         │                                                                   1. 🔀 **DECISION:** `$ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                      ➔ **If [false]:**
         │                                                                         1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty >= $LookupBidDataItem/PreviousRoundBidQuantity`
         │                                                                            ➔ **If [false]:**
         │                                                                            ➔ **If [true]:**
         │                                                                               1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                               2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [true]:**
         │                                                                   1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                      ➔ **If [false]:**
         │                                                          ➔ **If [false]:**
         │                                              ➔ **If [true]:**
         │                                                 1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidAmount =0 or $LookupBidDataItem/PreviousRoundBidAmount <= parseDecimal($IteratorImportedBidData/Price)`
         │                                                    ➔ **If [true]:**
         │                                                       1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $LookupBidDataItem/PreviousRoundBidQuantity = empty or $LookupBidDataItem/PreviousRoundBidQuantity = -1`
         │                                                          ➔ **If [false]:**
         │                                                             1. 🔀 **DECISION:** `$ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                ➔ **If [true]:**
         │                                                                   1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                   2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [false]:**
         │                                                                   1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty >= $LookupBidDataItem/PreviousRoundBidQuantity`
         │                                                                      ➔ **If [false]:**
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                          ➔ **If [true]:**
         │                                                             1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                ➔ **If [true]:**
         │                                                                   1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                   2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [false]:**
         │                                                    ➔ **If [false]:**
         │                                  ➔ **If [false]:**
         │                      ➔ **If [false]:**
         │                         1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Price is in an invalid format'`**
         │                         2. **Update Variable **$Failed** = `true`**
         │                ➔ **If [false]:**
         │                   1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Product ID is in an invalid format'`**
         │                   2. **Update Variable **$Failed** = `true`**
         │          ➔ **If [false]:**
         │             1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Available Quantity is in an invalid format'`**
         │             2. **Update Variable **$Failed** = `true`**
         │    ➔ **If [false]:**
         │       1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Capacity Quantity is in an invalid format'`**
         │       2. **Update Variable **$Failed** = `true`**
         └─ **End Loop**
      10. 🔀 **DECISION:** `$Failed != true`
         ➔ **If [true]:**
            1. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound and BidAmount != 0 and BidAmount != empty]` (Result: **$NotEmpty_Existing_BidData**)**
            2. 🔀 **DECISION:** `$ScheduleAuction/Round = 1 or $isSPTBuyer`
               ➔ **If [false]:**
                  1. **Delete**
                  2. **LogMessage**
                  3. 🏁 **END:** Return `$BidDataList_Updates`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$NotEmpty_Existing_BidData != empty`
                     ➔ **If [true]:**
                        1. **List Operation: **Subtract** on **$undefined** (Result: **$NewBidDataList**)**
                        2. 🔄 **LOOP:** For each **$IteratorBidData** in **$NewBidDataList**
                           │ 1. **Update **$IteratorBidData**
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`**
                           │ 2. **Add **$$IteratorBidData** to/from list **$BidDataList_Updates****
                           └─ **End Loop**
                        3. **Delete**
                        4. **LogMessage**
                        5. 🏁 **END:** Return `$BidDataList_Updates`
                     ➔ **If [false]:**
                        1. **Delete**
                        2. **LogMessage**
                        3. 🏁 **END:** Return `$BidDataList_Updates`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
      2. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound]` (Result: **$Existing_BidDataList**)**
      3. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction = $ScheduleAuction_PriorRound and AuctionUI.BidData_BuyerCode = $BidRound/AuctionUI.BidRound_BuyerCode]` (Result: **$Existing_BidDataList_PriorRound**)**
      4. **DB Retrieve **AuctionUI.BidData** Filter: `[Code = $NP_BuyerCodeSelect_Helper/Code] [AuctionUI.BidData_BidRound = $BidRound]` (Result: **$Existing_BidDataList_1**)**
      5. **CreateList**
      6. 🔄 **LOOP:** For each **$IteratorImportedBidData** in **$ExcelIMport_BidData**
         │ 1. **Call Microflow **EcoATM_BidData.SUB_BidData_ParseCapQty** (Result: **$ParsedCapQty**)**
         │ 2. 🔀 **DECISION:** `$ParsedCapQty != -999`
         │    ➔ **If [true]:**
         │       1. **Call Microflow **EcoATM_BidData.SUB_BidData_ParseAvailQty** (Result: **$ParsedAvailableQtyInteger**)**
         │       2. 🔀 **DECISION:** `$ParsedAvailableQtyInteger != -1`
         │          ➔ **If [true]:**
         │             1. **Call Microflow **EcoATM_BidData.SUB_BidData_ParseProductID** (Result: **$ParsedProductID**)**
         │             2. 🔀 **DECISION:** `$ParsedProductID != -1`
         │                ➔ **If [true]:**
         │                   1. **Create Variable **$ParsedPrice** = `parseDecimal($IteratorImportedBidData/Price)`**
         │                   2. 🔀 **DECISION:** `$ParsedPrice != -1`
         │                      ➔ **If [true]:**
         │                         1. 🔀 **DECISION:** `not(contains($IteratorImportedBidData/Price,'$'))`
         │                            ➔ **If [true]:**
         │                               1. **Update **$IteratorImportedBidData**
      - Set **Price** = `trim($IteratorImportedBidData/Price)`**
         │                               2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $ParsedProductID and $currentObject/Merged_Grade = $IteratorImportedBidData/Grade` (Result: **$LookupBidDataItem**)**
         │                               3. 🔀 **DECISION:** `$LookupBidDataItem != empty`
         │                                  ➔ **If [true]:**
         │                                     1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $ParsedProductID and $currentObject/Merged_Grade = $IteratorImportedBidData/Grade and $currentObject/BidAmount > 0` (Result: **$LookupBidDataItem_PriorRound**)**
         │                                     2. 🔀 **DECISION:** `$ScheduleAuction/Round = 1 or $isSPTBuyer or $LookupBidDataItem_PriorRound = empty`
         │                                        ➔ **If [true]:**
         │                                           1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                           2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                        ➔ **If [false]:**
         │                                           1. 🔀 **DECISION:** `$ScheduleAuction/Round = 2`
         │                                              ➔ **If [false]:**
         │                                                 1. 🔀 **DECISION:** `$ScheduleAuction/Round = 3`
         │                                                    ➔ **If [false]:**
         │                                                    ➔ **If [true]:**
         │                                                       1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidAmount =0 or $LookupBidDataItem/PreviousRoundBidAmount <= parseDecimal($IteratorImportedBidData/Price)`
         │                                                          ➔ **If [true]:**
         │                                                             1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $LookupBidDataItem/PreviousRoundBidQuantity = empty or $LookupBidDataItem/PreviousRoundBidQuantity = -1`
         │                                                                ➔ **If [false]:**
         │                                                                   1. 🔀 **DECISION:** `$ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                      ➔ **If [false]:**
         │                                                                         1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty >= $LookupBidDataItem/PreviousRoundBidQuantity`
         │                                                                            ➔ **If [false]:**
         │                                                                            ➔ **If [true]:**
         │                                                                               1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                               2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [true]:**
         │                                                                   1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                      ➔ **If [false]:**
         │                                                          ➔ **If [false]:**
         │                                              ➔ **If [true]:**
         │                                                 1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidAmount =0 or $LookupBidDataItem/PreviousRoundBidAmount <= parseDecimal($IteratorImportedBidData/Price)`
         │                                                    ➔ **If [true]:**
         │                                                       1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $LookupBidDataItem/PreviousRoundBidQuantity = empty or $LookupBidDataItem/PreviousRoundBidQuantity = -1`
         │                                                          ➔ **If [false]:**
         │                                                             1. 🔀 **DECISION:** `$ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                ➔ **If [true]:**
         │                                                                   1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                   2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [false]:**
         │                                                                   1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty >= $LookupBidDataItem/PreviousRoundBidQuantity`
         │                                                                      ➔ **If [false]:**
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                          ➔ **If [true]:**
         │                                                             1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                ➔ **If [true]:**
         │                                                                   1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                   2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [false]:**
         │                                                    ➔ **If [false]:**
         │                                  ➔ **If [false]:**
         │                            ➔ **If [false]:**
         │                               1. **Update **$IteratorImportedBidData**
      - Set **Price** = `replaceAll($IteratorImportedBidData/Price,'$','')`**
         │                               2. **Update **$IteratorImportedBidData**
      - Set **Price** = `trim($IteratorImportedBidData/Price)`**
         │                               3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $ParsedProductID and $currentObject/Merged_Grade = $IteratorImportedBidData/Grade` (Result: **$LookupBidDataItem**)**
         │                               4. 🔀 **DECISION:** `$LookupBidDataItem != empty`
         │                                  ➔ **If [true]:**
         │                                     1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID = $ParsedProductID and $currentObject/Merged_Grade = $IteratorImportedBidData/Grade and $currentObject/BidAmount > 0` (Result: **$LookupBidDataItem_PriorRound**)**
         │                                     2. 🔀 **DECISION:** `$ScheduleAuction/Round = 1 or $isSPTBuyer or $LookupBidDataItem_PriorRound = empty`
         │                                        ➔ **If [true]:**
         │                                           1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                           2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                        ➔ **If [false]:**
         │                                           1. 🔀 **DECISION:** `$ScheduleAuction/Round = 2`
         │                                              ➔ **If [false]:**
         │                                                 1. 🔀 **DECISION:** `$ScheduleAuction/Round = 3`
         │                                                    ➔ **If [false]:**
         │                                                    ➔ **If [true]:**
         │                                                       1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidAmount =0 or $LookupBidDataItem/PreviousRoundBidAmount <= parseDecimal($IteratorImportedBidData/Price)`
         │                                                          ➔ **If [true]:**
         │                                                             1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $LookupBidDataItem/PreviousRoundBidQuantity = empty or $LookupBidDataItem/PreviousRoundBidQuantity = -1`
         │                                                                ➔ **If [false]:**
         │                                                                   1. 🔀 **DECISION:** `$ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                      ➔ **If [false]:**
         │                                                                         1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty >= $LookupBidDataItem/PreviousRoundBidQuantity`
         │                                                                            ➔ **If [false]:**
         │                                                                            ➔ **If [true]:**
         │                                                                               1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                               2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [true]:**
         │                                                                   1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                      ➔ **If [false]:**
         │                                                          ➔ **If [false]:**
         │                                              ➔ **If [true]:**
         │                                                 1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidAmount =0 or $LookupBidDataItem/PreviousRoundBidAmount <= parseDecimal($IteratorImportedBidData/Price)`
         │                                                    ➔ **If [true]:**
         │                                                       1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $LookupBidDataItem/PreviousRoundBidQuantity = empty or $LookupBidDataItem/PreviousRoundBidQuantity = -1`
         │                                                          ➔ **If [false]:**
         │                                                             1. 🔀 **DECISION:** `$ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                ➔ **If [true]:**
         │                                                                   1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                   2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [false]:**
         │                                                                   1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty >= $LookupBidDataItem/PreviousRoundBidQuantity`
         │                                                                      ➔ **If [false]:**
         │                                                                      ➔ **If [true]:**
         │                                                                         1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                         2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                          ➔ **If [true]:**
         │                                                             1. 🔀 **DECISION:** `$LookupBidDataItem/PreviousRoundBidQuantity = 0 or $ParsedCapQty = empty or $ParsedCapQty = -1`
         │                                                                ➔ **If [true]:**
         │                                                                   1. **Update **$LookupBidDataItem**
      - Set **BidAmount** = `parseDecimal($IteratorImportedBidData/Price)`
      - Set **BidQuantity** = `$ParsedCapQty`
      - Set **BidAmount** = `$ParsedPrice`**
         │                                                                   2. **Add **$$LookupBidDataItem** to/from list **$BidDataList_Updates****
         │                                                                ➔ **If [false]:**
         │                                                    ➔ **If [false]:**
         │                                  ➔ **If [false]:**
         │                      ➔ **If [false]:**
         │                         1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Price is in an invalid format'`**
         │                         2. **Update Variable **$Failed** = `true`**
         │                ➔ **If [false]:**
         │                   1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Product ID is in an invalid format'`**
         │                   2. **Update Variable **$Failed** = `true`**
         │          ➔ **If [false]:**
         │             1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Available Quantity is in an invalid format'`**
         │             2. **Update Variable **$Failed** = `true`**
         │    ➔ **If [false]:**
         │       1. **Update **$ImportFile**
      - Set **ErrorMessage** = `'Capacity Quantity is in an invalid format'`**
         │       2. **Update Variable **$Failed** = `true`**
         └─ **End Loop**
      7. 🔀 **DECISION:** `$Failed != true`
         ➔ **If [true]:**
            1. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound and BidAmount != 0 and BidAmount != empty]` (Result: **$NotEmpty_Existing_BidData**)**
            2. 🔀 **DECISION:** `$ScheduleAuction/Round = 1 or $isSPTBuyer`
               ➔ **If [false]:**
                  1. **Delete**
                  2. **LogMessage**
                  3. 🏁 **END:** Return `$BidDataList_Updates`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$NotEmpty_Existing_BidData != empty`
                     ➔ **If [true]:**
                        1. **List Operation: **Subtract** on **$undefined** (Result: **$NewBidDataList**)**
                        2. 🔄 **LOOP:** For each **$IteratorBidData** in **$NewBidDataList**
                           │ 1. **Update **$IteratorBidData**
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `-1`**
                           │ 2. **Add **$$IteratorBidData** to/from list **$BidDataList_Updates****
                           └─ **End Loop**
                        3. **Delete**
                        4. **LogMessage**
                        5. 🏁 **END:** Return `$BidDataList_Updates`
                     ➔ **If [false]:**
                        1. **Delete**
                        2. **LogMessage**
                        3. 🏁 **END:** Return `$BidDataList_Updates`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [List] value.