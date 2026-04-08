# Microflow Detailed Specification: SUB_ProcessHistoricalBids

### 📥 Inputs (Parameters)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$BidRound** (Type: AuctionUI.BidRound)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **DB Retrieve **EcoATM_BidData.HistoricalBidData** Filter: `[buyer_code=$NP_BuyerCodeSelect_Helper/Code]` (Result: **$HistoricalBidDataList**)**
3. **CreateList**
4. 🔄 **LOOP:** For each **$IteratorHistoricalBidData** in **$HistoricalBidDataList**
   │ 1. **Create **AuctionUI.BidData** (Result: **$HistoricalBidDataObject**)
      - Set **EcoID** = `$IteratorHistoricalBidData/eco_id`
      - Set **BidQuantity** = `$IteratorHistoricalBidData/qty_cap`
      - Set **BidAmount** = `$IteratorHistoricalBidData/price`
      - Set **Merged_Grade** = `$IteratorHistoricalBidData/grade`**
   │ 2. **Add **$$HistoricalBidDataObject** to/from list **$BidDataList****
   └─ **End Loop**
5. **Call Microflow **EcoATM_BidData.ACT_AddCarryOverBidData****
6. **LogMessage**
7. 🏁 **END:** Return `$BidDataList`

**Final Result:** This process concludes by returning a [List] value.