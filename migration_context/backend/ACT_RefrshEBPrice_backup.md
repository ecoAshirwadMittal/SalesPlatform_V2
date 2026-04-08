# Microflow Detailed Specification: ACT_RefrshEBPrice_backup

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
2. **Call Microflow **AuctionUI.ACT_GetTimeOffset** (Result: **$TimeOffset**)**
3. **Call Microflow **EcoATM_EB.ACT_EB_Sync_Get_Monday_Date** (Result: **$mondaydate**)**
4. **ExecuteDatabaseQuery**
5. 🔀 **DECISION:** `$EBPriceList != empty`
   ➔ **If [Else]:**
      1. **CreateList**
      2. **DB Retrieve **EcoATM_EB.ReserveBid**  (Result: **$ExistingReserveBid_CurrentWeek_1**)**
      3. 🔄 **LOOP:** For each **$IteratorEBPrice** in **$EBPriceList**
         │ 1. **DB Retrieve **EcoATM_MDM.MasterDeviceInventory** Filter: `[ECOATM_CODE = $IteratorEBPrice/ECOATM_CODE]` (Result: **$MasterDeviceInventory**)**
         │ 2. **DB Retrieve **EcoATM_EB.ReserveBid** Filter: `[EcoATM_EB.ReserveBid_WeekImported = $Week] [ProductId = $IteratorEBPrice/ECOATM_CODE] [Grade = $IteratorEBPrice/MERGEDGRADE]` (Result: **$ExistingReserveBid_CurrentWeek**)**
         │ 3. 🔀 **DECISION:** `$ExistingReserveBid_CurrentWeek = empty`
         │    ➔ **If [Else]:**
         │       1. **DB Retrieve **EcoATM_EB.ReserveBid** Filter: `[ProductId = $IteratorEBPrice/ECOATM_CODE] [Grade = $IteratorEBPrice/GRADE]` (Result: **$ExistingReserveBid_LastWeek**)**
         │       2. 🔀 **DECISION:** `$ExistingReserveBid_LastWeek != empty`
         │          ➔ **If [Else]:**
         │             1. **Create **EcoATM_EB.ReserveBid** (Result: **$NewReserveBid**)
      - Set **ProductId** = `$IteratorEBPrice/ECOATM_CODE`
      - Set **Grade** = `$IteratorEBPrice/MERGEDGRADE`
      - Set **Bid** = `$IteratorEBPrice/BID`
      - Set **LastAwardedMinPrice** = `$IteratorEBPrice/MIN_BID`
      - Set **LastUpdateDateTime** = `subtractHours([%CurrentDateTime%],$TimeOffset)`
      - Set **ReserveBid_LastAwardedWeek** = `$Week`
      - Set **ReserveBid_WeekImported** = `$Week`
      - Set **ReserveBid_MasterDeviceInventory** = `$MasterDeviceInventory`**
         │             2. **Add **$$NewReserveBid
** to/from list **$ReserveBidList****
         │          ➔ **If [Else]:**
         │             1. 🔀 **DECISION:** `$ExistingReserveBid_LastWeek/Bid != $IteratorEBPrice/BID`
         │                ➔ **If [Else]:**
         │                   1. 🔀 **DECISION:** `$ExistingReserveBid_LastWeek/LastAwardedMinPrice != $IteratorEBPrice/MIN_BID`
         │                      ➔ **If [Else]:**
         │                         1. **Create **EcoATM_EB.ReserveBid** (Result: **$NewReserveBid**)
      - Set **ProductId** = `$IteratorEBPrice/ECOATM_CODE`
      - Set **Grade** = `$IteratorEBPrice/MERGEDGRADE`
      - Set **Bid** = `$IteratorEBPrice/BID`
      - Set **LastAwardedMinPrice** = `$IteratorEBPrice/MIN_BID`
      - Set **LastUpdateDateTime** = `subtractHours([%CurrentDateTime%],$TimeOffset)`
      - Set **ReserveBid_LastAwardedWeek** = `$Week`
      - Set **ReserveBid_WeekImported** = `$Week`
      - Set **ReserveBid_MasterDeviceInventory** = `$MasterDeviceInventory`**
         │                         2. **Add **$$NewReserveBid
** to/from list **$ReserveBidList****
         │                      ➔ **If [Else]:**
         │                         1. **Create **EcoATM_EB.ReserveBid** (Result: **$NewReserveBid_1**)
      - Set **ProductId** = `$IteratorEBPrice/ECOATM_CODE`
      - Set **Grade** = `$IteratorEBPrice/MERGEDGRADE`
      - Set **Bid** = `$ExistingReserveBid_LastWeek/Bid`
      - Set **LastAwardedMinPrice** = `$ExistingReserveBid_LastWeek/LastAwardedMinPrice`
      - Set **LastUpdateDateTime** = `$ExistingReserveBid_LastWeek/LastUpdateDateTime`
      - Set **ReserveBid_LastAwardedWeek** = `$ExistingReserveBid_LastWeek/EcoATM_EB.ReserveBid_LastAwardedWeek`
      - Set **ReserveBid_WeekImported** = `$Week`
      - Set **ReserveBid_MasterDeviceInventory** = `$MasterDeviceInventory`**
         │                         2. **Add **$$NewReserveBid_1
** to/from list **$ReserveBidList****
         │                ➔ **If [Else]:**
         │                   1. **Create **EcoATM_EB.ReserveBid** (Result: **$NewReserveBid**)
      - Set **ProductId** = `$IteratorEBPrice/ECOATM_CODE`
      - Set **Grade** = `$IteratorEBPrice/MERGEDGRADE`
      - Set **Bid** = `$IteratorEBPrice/BID`
      - Set **LastAwardedMinPrice** = `$IteratorEBPrice/MIN_BID`
      - Set **LastUpdateDateTime** = `subtractHours([%CurrentDateTime%],$TimeOffset)`
      - Set **ReserveBid_LastAwardedWeek** = `$Week`
      - Set **ReserveBid_WeekImported** = `$Week`
      - Set **ReserveBid_MasterDeviceInventory** = `$MasterDeviceInventory`**
         │                   2. **Add **$$NewReserveBid
** to/from list **$ReserveBidList****
         │    ➔ **If [Else]:**
         │       1. 🔀 **DECISION:** `$ExistingReserveBid_CurrentWeek/Bid != $IteratorEBPrice/BID`
         │          ➔ **If [Else]:**
         │             1. **Update **$ExistingReserveBid_CurrentWeek**
      - Set **Bid** = `$IteratorEBPrice/BID`
      - Set **LastUpdateDateTime** = `subtractHours([%CurrentDateTime%],$TimeOffset)`
      - Set **LastAwardedMinPrice** = `$IteratorEBPrice/MIN_BID`
      - Set **ReserveBid_LastAwardedWeek** = `$Week`
      - Set **ReserveBid_MasterDeviceInventory** = `$MasterDeviceInventory`**
         │             2. **Add **$$ExistingReserveBid_CurrentWeek
** to/from list **$ReserveBidList****
         │          ➔ **If [Else]:**
         │             1. 🔀 **DECISION:** `$ExistingReserveBid_CurrentWeek/LastAwardedMinPrice != $IteratorEBPrice/MIN_BID`
         │                ➔ **If [Else]:**
         │                   1. **Update **$ExistingReserveBid_CurrentWeek**
      - Set **Bid** = `$IteratorEBPrice/BID`
      - Set **LastUpdateDateTime** = `subtractHours([%CurrentDateTime%],$TimeOffset)`
      - Set **LastAwardedMinPrice** = `$IteratorEBPrice/MIN_BID`
      - Set **ReserveBid_LastAwardedWeek** = `$Week`
      - Set **ReserveBid_MasterDeviceInventory** = `$MasterDeviceInventory`**
         │                   2. **Add **$$ExistingReserveBid_CurrentWeek
** to/from list **$ReserveBidList****
         │                ➔ **If [Else]:**
         └─ **End Loop**
      4. **Commit/Save **$ReserveBidList** to Database**
      5. **Call Microflow **EcoATM_EB.ACT_GetOrCreateReserveBidSync** (Result: **$ReserveBidSync**)**
      6. 🏁 **END:** Return empty
   ➔ **If [Else]:**
      1. **LogMessage**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.