# Nanoflow: ACT_OpenInventoryAuctionOverview_ForSelectedAuction

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.SalesRep

## 📥 Inputs

- **$AggInventoryHelper** (AuctionUI.AggInventoryHelper)

## ⚙️ Execution Flow

1. **Retrieve related **AggInventoryHelper_Auction** via Association from **$AggInventoryHelper** (Result: **$SelectedAuction**)**
2. **Retrieve related **Auction_Week** via Association from **$SelectedAuction** (Result: **$Week**)**
3. **Retrieve related **SchedulingAuction_Helper_AggInventoryHelper** via Association from **$AggInventoryHelper** (Result: **$SchedulingAuction_HelperList**)**
4. **List Operation: **FindByExpression** on **$SchedulingAuction_HelperList** where `$currentObject/AuctionUI.SchedulingAuction_Helper_Auction=$SelectedAuction` (Result: **$SchedulingAuction_Helper_Auction**)**
5. 🔀 **DECISION:** `$SchedulingAuction_Helper_Auction!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_OpenInventoryOverviewForSelectedAuction****
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Create **AuctionUI.SchedulingAuction_Helper** (Result: **$SchedulingAuction_Helper_Auction1**)
      - Set **SchedulingAuction_Helper_AggInventoryHelper** = `$AggInventoryHelper`
      - Set **SchedulingAuction_Helper_Auction** = `$SelectedAuction`**
      2. **Call Microflow **AuctionUI.ACT_OpenInventoryOverviewForSelectedAuction****
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
