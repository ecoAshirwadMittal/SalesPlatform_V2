# Microflow Detailed Specification: ACT_InventoryNotification

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[(RoundStatus ='Started') and (HasRound = true) and ((Start_DateTime <= '[%CurrentDateTime%]') or (End_DateTime<='[%CurrentDateTime%]')) ]` (Result: **$SchedulingAuctionList**)**
2. **Call Microflow **AuctionUI.ACT_GetTimeOffset** (Result: **$TimeZoneOffset**)**
3. **Create Variable **$CurrentDT** = `subtractHours(trimToMinutes([%CurrentDateTime%]),$TimeZoneOffset)`**
4. 🔄 **LOOP:** For each **$IteratorSchedulingAuction** in **$SchedulingAuctionList**
   │ 1. **LogMessage**
   │ 2. **Call Microflow **AuctionUI.SUB_InvenNotificationProcessDateTime****
   └─ **End Loop**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.