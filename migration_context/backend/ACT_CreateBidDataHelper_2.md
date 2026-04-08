# Microflow Detailed Specification: ACT_CreateBidDataHelper_2

### 📥 Inputs (Parameters)
- **$BuyerCodeSelect_Helper** (Type: AuctionUI.BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
2. **Create Variable **$oqlQuery** = `' SELECT AI.ecoId AS EcoID, AI.Model_Name, AI.Brand, AI.Model, AI.Carrier, AI.ecoGrade, AI.Data_Wipe_Quantity AS MaximumQuantity, 0 AS Total_Quantity, COALESCE(BD.BidQuantity, 0) AS BidQuantity, COALESCE(BD.BidAmount, 0) AS BidAmount FROM AuctionUI.AgregatedInventory AS AI LEFT JOIN AI/AuctionUI.BidData_AgregatedInventory/AuctionUI.BidData AS BD ON BD/AuctionUI.BidData_EcoATMDirectUser/AuctionUI.EcoATMDirectUser/Name = $currentUser/Name AND BD/AuctionUI.BidData_BuyerCode/AuctionUI.BuyerCode/Code = $BuyerCodeSelect_Helper/Code WHERE AI.ecoId = $IteratorAgregatedInventory/ecoID AND AI.ecoGrade = $IteratorAgregatedInventory/ecoGrade AND AI/AuctionUI.AgregatedInventory_Week = $Week'`**
3. **JavaCallAction**
4. **DB Retrieve **AuctionUI.AggregatedInventory**  (Result: **$AgregatedInventoryList**)**
5. **CreateList**
6. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventoryList**
   │ 1. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_EcoATMDirectUser/AuctionUI.EcoATMDirectUser/Name=$currentUser/Name and AuctionUI.BidData_BuyerCode/AuctionUI.BuyerCode/Code=$BuyerCodeSelect_Helper/Code and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/ecoID=$IteratorAgregatedInventory/ecoID and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Merged_Grade=$IteratorAgregatedInventory/Merged_Grade and AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week=$Week]` (Result: **$ExistingBidData**)**
   │ 2. 🔀 **DECISION:** `$ExistingBidData!=empty`
   │    ➔ **If [true]:**
   │       1. **Create **AuctionUI.BidData_Helper** (Result: **$UpdateBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/ecoID`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidQuantity** = `$ExistingBidData/BidQuantity`
      - Set **BidAmount** = `$ExistingBidData/BidAmount`
      - Set **TargetPrice** = `0`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/Merged_Grade`
      - Set **Code** = `$BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BuyerCode** = `$BuyerCodeSelect_Helper/AuctionUI.BuyerCodeSelect_Helper_BuyerCode`
      - Set **WeekId** = `$Week/WeekID`
      - Set **CompanyName** = `$BuyerCodeSelect_Helper/CompanyName`**
   │       2. **Add **$$UpdateBidData_Helper** to/from list **$BidData_HelperList****
   │    ➔ **If [false]:**
   │       1. **Create **AuctionUI.BidData_Helper** (Result: **$NewBidData_Helper**)
      - Set **EcoID** = `$IteratorAgregatedInventory/ecoID`
      - Set **BidData_Helper_AggregatedInventory** = `$IteratorAgregatedInventory`
      - Set **BidAmount** = `0`
      - Set **BidQuantity** = `0`
      - Set **ecoGrade** = `$IteratorAgregatedInventory/Merged_Grade`
      - Set **Code** = `$BuyerCodeSelect_Helper/Code`
      - Set **BidData_Helper_BuyerCode** = `$BuyerCodeSelect_Helper/AuctionUI.BuyerCodeSelect_Helper_BuyerCode`
      - Set **WeekId** = `$Week/WeekID`
      - Set **User** = `$currentUser/Name`
      - Set **CompanyName** = `$BuyerCodeSelect_Helper/CompanyName`**
   │       2. **Add **$$NewBidData_Helper** to/from list **$BidData_HelperList****
   └─ **End Loop**
7. **LogMessage**
8. 🏁 **END:** Return `$BidData_HelperList`

**Final Result:** This process concludes by returning a [List] value.