# Microflow Detailed Specification: SUB_BidData_Transform_Round3BidRank

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$ExcelIMport_BidData** (Type: Custom_Excel_Import.BidDataRankRound3Export)
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
      3. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
      4. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound = $BidRound]` (Result: **$Existing_BidDataList**)**
      5. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction = $ScheduleAuction_PriorRound and AuctionUI.BidData_BuyerCode = $BidRound/AuctionUI.BidRound_BuyerCode]` (Result: **$Existing_BidDataList_PriorRound**)**
      6. **CreateList**
      7. 🔄 **LOOP:** For each **$IteratorImportedBidData** in **$ExcelIMport_BidData**
         └─ **End Loop**
      8. 🔀 **DECISION:** `$Failed != true`
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
      4. **CreateList**
      5. 🔄 **LOOP:** For each **$IteratorImportedBidData** in **$ExcelIMport_BidData**
         └─ **End Loop**
      6. 🔀 **DECISION:** `$Failed != true`
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