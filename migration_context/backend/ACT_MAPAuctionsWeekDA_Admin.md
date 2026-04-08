# Microflow Detailed Specification: ACT_MAPAuctionsWeekDA_Admin

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **AuctionUI.Auction**  (Result: **$AuctionList**)**
3. 🔄 **LOOP:** For each **$IteratorAuction** in **$AuctionList**
   │ 1. **Retrieve related **Auction_Week** via Association from **$IteratorAuction** (Result: **$Week**)**
   │ 2. **Call Microflow **EcoATM_DA.SUB_DAWeek_GetOrCreate** (Result: **$DAWeek**)**
   └─ **End Loop**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.