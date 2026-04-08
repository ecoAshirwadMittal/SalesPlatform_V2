# Nanoflow: ACT_GetAggregateInventoryforWeek

**Allowed Roles:** AuctionUI.Administrator, AuctionUI.Bidder, AuctionUI.SalesOps, AuctionUI.SalesRep

## 📥 Inputs

- **$AggInventoryHelper** (AuctionUI.AggInventoryHelper)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Retrieve related **AggInventoryHelper_Week** via Association from **$AggInventoryHelper** (Result: **$SelectedWeek**)**
3. 🔀 **DECISION:** `$SelectedWeek !=empty`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_SetAggregatedIventoryHelper****
      2. **Open Page: **AuctionUI.PG_AggregatedInventory****
      3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `Please select Week`**
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
