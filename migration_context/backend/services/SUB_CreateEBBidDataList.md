# Microflow Detailed Specification: SUB_CreateEBBidDataList

### 📥 Inputs (Parameters)
- **$EBBuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. **DB Retrieve **EcoATM_DA.DeviceBuyer** Filter: `[EcoATM_DA.DeviceBuyer_DAWeek = $Week/EcoATM_DA.DAWeek_Week] [IsChanged] [EB]` (Result: **$DeviceBuyerList**)**
3. 🔄 **LOOP:** For each **$IteratorDeviceBuyer** in **$DeviceBuyerList**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject = $IteratorDeviceBuyer/EcoATM_DA.DeviceBuyer_BidData/AuctionUI.BidData` (Result: **$ExistingEBBidData**)**
   │ 2. 🔀 **DECISION:** `$ExistingEBBidData = empty`
   │    ➔ **If [false]:**
   │    ➔ **If [true]:**
   │       1. **Retrieve related **DeviceBuyer_DeviceAllocation** via Association from **$IteratorDeviceBuyer** (Result: **$DeviceAllocation**)**
   │       2. **DB Retrieve **AuctionUI.AggregatedInventory** Filter: `[ ( EcoId = $DeviceAllocation/ProductID and MergedGrade = $DeviceAllocation/Grade and AuctionUI.AggregatedInventory_Week = $Week ) ]` (Result: **$AggregatedInventoryItem**)**
   │       3. **Create **AuctionUI.BidData** (Result: **$NewEBBidData**)
      - Set **EcoID** = `$DeviceAllocation/ProductID`
      - Set **BidAmount** = `$IteratorDeviceBuyer/Bid`
      - Set **Code** = `'EB'`
      - Set **Merged_Grade** = `$DeviceAllocation/Grade`
      - Set **BidData_BuyerCode** = `$EBBuyerCode`
      - Set **DeviceBuyer_BidData** = `$IteratorDeviceBuyer`
      - Set **BidData_AggregatedInventory** = `$AggregatedInventoryItem`**
   │       4. **Add **$$NewEBBidData
** to/from list **$EBBidDataList****
   └─ **End Loop**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.