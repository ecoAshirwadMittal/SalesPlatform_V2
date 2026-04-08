# Microflow Detailed Specification: Sub_BidDataSanitize_2

### 📥 Inputs (Parameters)
- **$BidDataList** (Type: AuctionUI.BidData)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **Filter** on **$undefined** where `-1` (Result: **$BidDataListMinus1**)**
2. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$BidDataListMinusEmpty**)**
3. **List Operation: **Filter** on **$undefined** where `0` (Result: **$BidDataListMinusZero**)**
4. **List Operation: **Union** on **$undefined** (Result: **$BidDataListMinusReady**)**
5. **List Operation: **Subtract** on **$undefined** (Result: **$BidDataListSansMinus1New**)**
6. 🔄 **LOOP:** For each **$IteratorBidDataNew** in **$BidDataListSansMinus1New**
   │ 1. **Update **$IteratorBidDataNew**
      - Set **BidQuantity** = `if $IteratorBidDataNew/BidQuantity < 0 then 0 else $IteratorBidDataNew/BidQuantity`**
   └─ **End Loop**
7. 🔄 **LOOP:** For each **$IteratorBidDataMinusReadyNew** in **$BidDataListMinusReady**
   │ 1. 🔀 **DECISION:** `$IteratorBidDataMinusReadyNew/BidAmount != empty or $IteratorBidDataMinusReadyNew/BidAmount != -1`
   │    ➔ **If [true]:**
   │       1. **Update **$IteratorBidDataMinusReadyNew**
      - Set **BidQuantity** = `if $IteratorBidDataMinusReadyNew/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Total_Quantity != empty then $IteratorBidDataMinusReadyNew/AuctionUI.BidData_AggregatedInventory/AuctionUI.AggregatedInventory/Total_Quantity else 0`**
   │       2. **Add **$$IteratorBidDataMinusReadyNew** to/from list **$BidDataListSansMinus1New****
   │    ➔ **If [false]:**
   └─ **End Loop**
8. **CreateList**
9. **CreateList**
10. **CreateList**
11. 🔄 **LOOP:** For each **$IteratorBidDataHelper** in **$BidDataListSansMinus1New**
   │ 1. **Create **AuctionUI.BidDataDuplicateHelper** (Result: **$NewBidDataDuplicateHelper**)
      - Set **EcoID** = `$IteratorBidDataHelper/EcoID`
      - Set **Merged_Grade** = `$IteratorBidDataHelper/Merged_Grade`
      - Set **Round** = `$IteratorBidDataHelper/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round`
      - Set **Code** = `$IteratorBidDataHelper/AuctionUI.BidData_BuyerCode/AuctionUI.BuyerCode/Code`
      - Set **BidDataDuplicateHelper_BidData** = `$IteratorBidDataHelper`**
   │ 2. **Add **$$NewBidDataDuplicateHelper** to/from list **$BidDataDuplicateHelperList****
   └─ **End Loop**
12. 🔄 **LOOP:** For each **$IteratorBidDataOuter** in **$BidDataDuplicateHelperList**
   │ 1. **List Operation: **Filter** on **$undefined** where `$IteratorBidDataOuter/EcoID` (Result: **$FilteredecoID**)**
   │ 2. 🔀 **DECISION:** `$FilteredecoID = empty`
   │    ➔ **If [true]:**
   │       1. **Add **$$IteratorBidDataOuter/AuctionUI.BidDataDuplicateHelper_BidData** to/from list **$UniqueBidDataList****
   │    ➔ **If [false]:**
   │       1. **List Operation: **Filter** on **$undefined** where `$IteratorBidDataOuter/Merged_Grade` (Result: **$FilteredMergeGrade**)**
   │       2. 🔀 **DECISION:** `$FilteredMergeGrade = empty`
   │          ➔ **If [true]:**
   │             1. **Add **$$IteratorBidDataOuter/AuctionUI.BidDataDuplicateHelper_BidData** to/from list **$UniqueBidDataList****
   │          ➔ **If [false]:**
   │             1. **List Operation: **Filter** on **$undefined** where `$IteratorBidDataOuter/AuctionUI.BidDataDuplicateHelper_BidData/AuctionUI.BidData/AuctionUI.BidData_BuyerCode` (Result: **$FilteredAssociation**)**
   │             2. 🔀 **DECISION:** `$FilteredAssociation = empty`
   │                ➔ **If [true]:**
   │                   1. **Add **$$IteratorBidDataOuter/AuctionUI.BidDataDuplicateHelper_BidData** to/from list **$UniqueBidDataList****
   │                ➔ **If [false]:**
   │                   1. **List Operation: **Intersect** on **$undefined** (Result: **$IntersectList**)**
   │                   2. 🔀 **DECISION:** `$IntersectList = empty`
   │                      ➔ **If [true]:**
   │                         1. **Add **$$IteratorBidDataOuter/AuctionUI.BidDataDuplicateHelper_BidData** to/from list **$UniqueBidDataList****
   │                      ➔ **If [false]:**
   │                         1. 🔄 **LOOP:** For each **$IteratorBidDataInner** in **$IntersectList**
   │                            │ 1. 🔀 **DECISION:** `$IteratorBidDataInner/AuctionUI.BidData_BuyerCode/AuctionUI.BuyerCode/Code = $IteratorBidDataOuter/Code and $IteratorBidDataInner/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/Round = $IteratorBidDataOuter/Round`
   │                            │    ➔ **If [true]:**
   │                            │    ➔ **If [false]:**
   │                            │       1. **Add **$$IteratorBidDataOuter/AuctionUI.BidDataDuplicateHelper_BidData** to/from list **$BidDataDuplicateList****
   │                            │       2. **Add **$$IteratorBidDataOuter/AuctionUI.BidDataDuplicateHelper_BidData** to/from list **$UniqueBidDataList****
   │                            └─ **End Loop**
   └─ **End Loop**
13. **CreateList**
14. 🔄 **LOOP:** For each **$IteratorBidData** in **$UniqueBidDataList**
   │ 1. **List Operation: **Intersect** on **$undefined** (Result: **$ReturnValueName**)**
   │ 2. 🔀 **DECISION:** `$ReturnValueName != empty`
   │    ➔ **If [true]:**
   │    ➔ **If [false]:**
   │       1. **Add **$$IteratorBidData** to/from list **$FinalChecker****
   └─ **End Loop**
15. **CreateList**
16. 🔄 **LOOP:** For each **$IteratorBidData_2** in **$BidDataListSansMinus1New**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorBidData_2` (Result: **$NewBidData**)**
   └─ **End Loop**
17. 🏁 **END:** Return `$FinalChecker`

**Final Result:** This process concludes by returning a [List] value.