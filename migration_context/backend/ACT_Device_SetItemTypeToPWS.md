# Microflow Detailed Specification: ACT_Device_SetItemTypeToPWS

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[ItemType= '' or ItemType=empty]` (Result: **$DeviceList_noItemType**)**
2. 🔀 **DECISION:** `$DeviceList_noItemType!=empty`
   ➔ **If [true]:**
      1. **CreateList**
      2. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList_noItemType**
         │ 1. **Update **$IteratorDevice**
      - Set **ItemType** = `'PWS'`**
         │ 2. **Add **$$IteratorDevice** to/from list **$DeviceList****
         └─ **End Loop**
      3. **Commit/Save **$DeviceList** to Database**
      4. **AggregateList**
      5. **Show Message (Information): `Updated {1} records!`**
      6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Information): `No records to update.`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.