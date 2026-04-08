# Microflow Detailed Specification: SUB_ResetBidDataAndDAData

### 📥 Inputs (Parameters)
- **$BidDataList** (Type: AuctionUI.BidData)
- **$DeviceBuyerList** (Type: EcoATM_DA.DeviceBuyer)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList**
   │ 1. **Update **$IteratorBidData**
      - Set **IsChanged** = `false`
      - Set **BidAmount** = `$IteratorBidData/TempDABidAmount`**
   └─ **End Loop**
3. 🔄 **LOOP:** For each **$IteratorDeviceBuyer** in **$DeviceBuyerList**
   │ 1. **Update **$IteratorDeviceBuyer**
      - Set **IsChanged** = `false`**
   │ 2. **Retrieve related **DeviceBuyer_DeviceAllocation** via Association from **$IteratorDeviceBuyer** (Result: **$DeviceAllocation**)**
   │ 3. **Update **$DeviceAllocation**
      - Set **IsChanged** = `false`**
   │ 4. **Add **$$DeviceAllocation** to/from list **$DeviceAllocationList****
   └─ **End Loop**
4. **Commit/Save **$BidDataList** to Database**
5. **Commit/Save **$DeviceBuyerList** to Database**
6. **Commit/Save **$DeviceAllocationList** to Database**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.