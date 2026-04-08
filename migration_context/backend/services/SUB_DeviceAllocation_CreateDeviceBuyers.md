# Microflow Detailed Specification: SUB_DeviceAllocation_CreateDeviceBuyers

### 📥 Inputs (Parameters)
- **$BidDataList** (Type: AuctionUI.BidData)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. **Create Variable **$BuyerJSON** = `$DeviceAllocation/BuyerJSON`**
3. **ImportXml**
4. **Retrieve related **NP_DeviceBuyer_Root** via Association from **$Root** (Result: **$NP_DeviceBuyerList**)**
5. 🔄 **LOOP:** For each **$IteratorBidData** in **$BidDataList**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorBidData/Code` (Result: **$NP_DeviceBuyer**)**
   │ 2. **Create **EcoATM_DA.DeviceBuyer** (Result: **$NewDeviceBuyer**)
      - Set **Bid** = `$IteratorBidData/BidAmount`
      - Set **QtyCap** = `$IteratorBidData/BidQuantity`
      - Set **BuyerCode** = `$IteratorBidData/Code`
      - Set **BuyerName** = `$IteratorBidData/AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/CompanyName`
      - Set **DeviceBuyer_DeviceAllocation** = `$DeviceAllocation`
      - Set **RejectReason** = `$IteratorBidData/RejectReason`
      - Set **Reject** = `$IteratorBidData/Rejected`
      - Set **DeviceBuyer_BidData** = `$IteratorBidData`
      - Set **DeviceBuyer_DAWeek** = `$DAWeek`
      - Set **AwardedQty** = `if $NP_DeviceBuyer!=empty then $NP_DeviceBuyer/QuantityAllocated else 0`**
   │ 3. **Add **$$NewDeviceBuyer
** to/from list **$DeviceBuyerListToCommit****
   └─ **End Loop**
6. **Create **EcoATM_DA.DeviceBuyer** (Result: **$ReserveBidToDeviceBuyer**)
      - Set **Bid** = `$DeviceAllocation/EB`
      - Set **BuyerCode** = `'EB'`
      - Set **BuyerName** = `'Reserve'`
      - Set **QtyCap** = `empty`
      - Set **DeviceBuyer_DeviceAllocation** = `$DeviceAllocation`
      - Set **EB** = `true`
      - Set **DeviceBuyer_DAWeek** = `$DAWeek`**
7. **Add **$$ReserveBidToDeviceBuyer
** to/from list **$DeviceBuyerListToCommit****
8. **Commit/Save **$DeviceBuyerListToCommit** to Database**
9. 🏁 **END:** Return `$DeviceBuyerListToCommit`

**Final Result:** This process concludes by returning a [List] value.