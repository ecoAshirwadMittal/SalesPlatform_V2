# Nanoflow: NA_NAV_DALandingPage

**Allowed Roles:** EcoATM_DA.Administrator, EcoATM_DA.SalesLeader, EcoATM_DA.SalesOps

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Progress**)**
2. **Call Microflow **EcoATM_DA.SUB_DAHelper_GerOrCreate** (Result: **$DAHelper**)**
3. **Call Microflow **EcoATM_DA.SUB_DAWeekFromDAHelper_GetCreate** (Result: **$DAWeek**)**
4. 🔀 **DECISION:** `$DAWeek !=empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_DA.SUB_GetSchedulingAuctionEndDate** (Result: **$AuctionEndDate**)**
      2. **Update **$DAHelper**
      - Set **DAHelper_DAWeek** = `$DAWeek`
      - Set **AuctionEndDate** = `$AuctionEndDate`**
      3. **Call Microflow **EcoATM_DA.SUB_GetDADataByWeek****
      4. **Call Microflow **Custom_Logging.SUB_Log_Info****
      5. **Open Page: **EcoATM_DA.PG_DeviceAllocation****
      6. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `No Auction Exists for this Week`**
      2. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
      3. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
