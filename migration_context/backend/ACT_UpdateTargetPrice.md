# Microflow Detailed Specification: ACT_UpdateTargetPrice

### 📥 Inputs (Parameters)
- **$SchedulingAuction** (Type: AuctionUI.SchedulingAuction)
- **$MaxLotBid** (Type: AuctionUI.MaxLotBid)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig** (Result: **$BuyerCodeSubmitConfig**)**
3. **Retrieve related **SchedulingAuction_Auction** via Association from **$SchedulingAuction** (Result: **$Auction**)**
4. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
5. **DB Retrieve **EcoATM_PO.PurchaseOrder** Filter: `[EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week/WeekID <= $Week/WeekID and EcoATM_PO.PurchaseOrder_Week_To/EcoATM_MDM.Week/WeekID >= $Week/WeekID]` (Result: **$PurchaseOrderForWeek**)**
6. **Create Variable **$RoundNumber** = `$SchedulingAuction/Round`**
7. **CreateList**
8. 🔄 **LOOP:** For each **$IteratorMaxLotBid** in **$MaxLotBid**
   │ 1. **Call Microflow **AuctionUI.SUB_TryUpdateAETargetPriceMaxBid****
   └─ **End Loop**
9. **Commit/Save **$UpdatedAggregatedInventoryList** to Database**
10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.