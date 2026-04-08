# Microflow Detailed Specification: ACT_CreateInventoryAggregate

### 📥 Inputs (Parameters)
- **$Week** (Type: AuctionUI.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$OQLQueryBarcodeCount_groupby** = `'select ecoID as ProductID, Merged_Grade as ProductGrade, count (Barcode) as TotalBarcodes from AuctionUI."Inventory" inner join AuctionUI.Inventory/AuctionUI.Inventory_Week/AuctionUI."Week" where WeekID = ' + toString($Week/WeekID) + ' group by ecoID, Merged_Grade'`**
2. **JavaCallAction**
3. **Create Variable **$OQLQueryBarcodeCount** = `'select WeekID, ecoID as ProductID, Model_Name as Model_Name, Brand as Brand, Model as Model, Carrier as Carrier, Merged_Grade as ProductGrade, TARGET_PRICE as TargetPrice, Data_Wipe as DataWipe, Device_Id, Category, Payout, count (Barcode) as TotalBarcodes from AuctionUI."Inventory" inner join AuctionUI.Inventory/AuctionUI.Inventory_Week/AuctionUI."Week" where WeekID = ' + toString($Week/WeekID) + ' group by WeekID, ecoID, Model_Name, Brand, Model, Carrier, Merged_Grade, Data_Wipe, Device_Id, Category, TARGET_PRICE, Payout'`**
4. **JavaCallAction**
5. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[(AuctionUI.AggregatedInventory_Week = $Week)]` (Result: **$ExistingAgregatedInventoryList**)**
6. **Delete**
7. **CreateList**
8. **CreateList**
9. 🔄 **LOOP:** For each **$IteratorAgregatedInventory** in **$AgregatedInventory_Count**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoId = $IteratorAgregatedInventory/ProductID and $currentObject/MergedGrade = $IteratorAgregatedInventory/ProductGrade` (Result: **$ExistingItem**)**
   │ 2. 🔀 **DECISION:** `$ExistingItem != empty`
   │    ➔ **If [false]:**
   │       1. 🔀 **DECISION:** `$IteratorAgregatedInventory/DataWipe = 'DW'`
   │          ➔ **If [false]:**
   │             1. **Create **AuctionUI.AgregatedInventory_BarcodeCount** (Result: **$NewAgregatedInventory_BarcodeCount_1_1**)
      - Set **ProductID** = `$IteratorAgregatedInventory/ProductID`
      - Set **Model_Name** = `$IteratorAgregatedInventory/Model_Name`
      - Set **Brand** = `$IteratorAgregatedInventory/Brand`
      - Set **Model** = `$IteratorAgregatedInventory/Model`
      - Set **Carrier** = `$IteratorAgregatedInventory/Carrier`
      - Set **WeekID** = `$Week/WeekID`
      - Set **TotalBarcodes** = `$IteratorAgregatedInventory/TotalBarcodes`
      - Set **ProductGrade** = `$IteratorAgregatedInventory/ProductGrade`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **Device_Id** = `$IteratorAgregatedInventory/Device_Id`
      - Set **Category** = `$IteratorAgregatedInventory/Category`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **Payout** = `$IteratorAgregatedInventory/Payout`**
   │             2. **Add **$$NewAgregatedInventory_BarcodeCount_1_1** to/from list **$AgregatedInventoryList_BarcodeCount_Processed****
   │             3. **Create **AuctionUI.AggregatedInventory** (Result: **$NewAgregatedInventory**)
      - Set **EcoId** = `$IteratorAgregatedInventory/ProductID`
      - Set **Name** = `$IteratorAgregatedInventory/Model_Name`
      - Set **Brand** = `$IteratorAgregatedInventory/Brand`
      - Set **Model** = `$IteratorAgregatedInventory/Model`
      - Set **Carrier** = `$IteratorAgregatedInventory/Carrier`
      - Set **AggregatedInventory_Week** = `$Week`
      - Set **TotalQuantity** = `$IteratorAgregatedInventory/TotalBarcodes`
      - Set **MergedGrade** = `$IteratorAgregatedInventory/ProductGrade`
      - Set **AvgTargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **DeviceId** = `$IteratorAgregatedInventory/Device_Id`
      - Set **Category** = `$IteratorAgregatedInventory/Category`
      - Set **AvgPayout** = `$IteratorAgregatedInventory/Payout`**
   │             4. **Add **$$NewAgregatedInventory** to/from list **$AgregatedInventoryList****
   │          ➔ **If [true]:**
   │             1. **Create **AuctionUI.AgregatedInventory_BarcodeCount** (Result: **$NewAgregatedInventory_BarcodeCount_1**)
      - Set **ProductID** = `$IteratorAgregatedInventory/ProductID`
      - Set **Model_Name** = `$IteratorAgregatedInventory/Model_Name`
      - Set **Brand** = `$IteratorAgregatedInventory/Brand`
      - Set **Model** = `$IteratorAgregatedInventory/Model`
      - Set **Carrier** = `$IteratorAgregatedInventory/Carrier`
      - Set **WeekID** = `$Week/WeekID`
      - Set **TotalBarcodes** = `$IteratorAgregatedInventory/TotalBarcodes`
      - Set **ProductGrade** = `$IteratorAgregatedInventory/ProductGrade`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **Device_Id** = `$IteratorAgregatedInventory/Device_Id`
      - Set **Category** = `$IteratorAgregatedInventory/Category`
      - Set **TargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **Payout** = `$IteratorAgregatedInventory/Payout`**
   │             2. **Add **$$NewAgregatedInventory_BarcodeCount_1** to/from list **$AgregatedInventoryList_BarcodeCount_Processed****
   │             3. **Create **AuctionUI.AggregatedInventory** (Result: **$NewAgregatedInventory_1**)
      - Set **EcoId** = `$IteratorAgregatedInventory/ProductID`
      - Set **Name** = `$IteratorAgregatedInventory/Model_Name`
      - Set **Brand** = `$IteratorAgregatedInventory/Brand`
      - Set **Model** = `$IteratorAgregatedInventory/Model`
      - Set **Carrier** = `$IteratorAgregatedInventory/Carrier`
      - Set **AggregatedInventory_Week** = `$Week`
      - Set **TotalQuantity** = `$IteratorAgregatedInventory/TotalBarcodes`
      - Set **MergedGrade** = `$IteratorAgregatedInventory/ProductGrade`
      - Set **DWAvgTargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **DeviceId** = `$IteratorAgregatedInventory/Device_Id`
      - Set **Category** = `$IteratorAgregatedInventory/Category`
      - Set **AvgTargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **AvgPayout** = `$IteratorAgregatedInventory/Payout`**
   │             4. **Add **$$NewAgregatedInventory_1** to/from list **$AgregatedInventoryList****
   │    ➔ **If [true]:**
   │       1. 🔀 **DECISION:** `$IteratorAgregatedInventory/DataWipe = 'DW'`
   │          ➔ **If [true]:**
   │             1. **Update **$ExistingItem**
      - Set **DWAvgTargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **DWTotalQuantity** = `$IteratorAgregatedInventory/TotalBarcodes`
      - Set **TotalQuantity** = `$IteratorAgregatedInventory/TotalBarcodes + $ExistingItem/TotalQuantity`
      - Set **AvgPayout** = `$ExistingItem/AvgPayout + $IteratorAgregatedInventory/Payout`**
   │          ➔ **If [false]:**
   │             1. **Update **$ExistingItem**
      - Set **AvgTargetPrice** = `$IteratorAgregatedInventory/TargetPrice`
      - Set **TotalQuantity** = `$ExistingItem/TotalQuantity + $IteratorAgregatedInventory/TotalBarcodes`
      - Set **AvgPayout** = `$ExistingItem/AvgPayout + $IteratorAgregatedInventory/Payout`**
   └─ **End Loop**
10. **Create Variable **$OQLQueryDataWipeCount** = `'select WeekID, ecoID as ProductID, Merged_Grade as ProductGrade, count (Barcode) as TotalDataWipes from AuctionUI."Inventory" inner join AuctionUI.Inventory/AuctionUI.Inventory_Week/AuctionUI."Week" where Data_Wipe = ''DW'' group by WeekID, ecoID,Merged_Grade having WeekID = ' + toString($Week/WeekID)`**
11. **List Operation: **Subtract** on **$undefined** (Result: **$AggregatedInventory_Subtract**)**
12. **List Operation: **Subtract** on **$undefined** (Result: **$AggregatedInventory_Subtract_GroupBy_Processed**)**
13. **List Operation: **Subtract** on **$undefined** (Result: **$AggregatedInventory_Subtract_GroupBy_Processed_2**)**
14. **JavaCallAction**
15. 🔄 **LOOP:** For each **$IteratorAgregatedInventory_DataWipeCount** in **$AgregatedInventory_DataWipeCount**
   │ 1. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoId =$IteratorAgregatedInventory_DataWipeCount/ProductID and $currentObject/MergedGrade= $IteratorAgregatedInventory_DataWipeCount/ProductGrade` (Result: **$MatchedAgregatedInventoryList**)**
   │ 2. **List Operation: **Head** on **$undefined** (Result: **$ItemToUpdate**)**
   │ 3. 🔀 **DECISION:** `$ItemToUpdate!= empty`
   │    ➔ **If [true]:**
   │       1. **Update **$ItemToUpdate**
      - Set **DWTotalQuantity** = `$IteratorAgregatedInventory_DataWipeCount/TotalDataWipes`**
   │    ➔ **If [false]:**
   │       1. **LogMessage**
   └─ **End Loop**
16. **Commit/Save **$AgregatedInventoryList** to Database**
17. **Delete**
18. **Delete**
19. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.