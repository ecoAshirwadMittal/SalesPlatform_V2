# Microflow Detailed Specification: ACT_Device_Submit

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Device_Session** via Association from **$currentSession** (Result: **$DeviceList**)**
2. **List Operation: **Filter** on **$undefined** where `true` (Result: **$DeviceList_Modified**)**
3. **CreateList**
4. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList_Modified**
   │ 1. **Retrieve related **Price_Device_Current** via Association from **$IteratorDevice** (Result: **$Price_Current**)**
   │ 2. **Add **$$Price_Current
** to/from list **$PriceList****
   │ 3. **Retrieve related **Price_Device_Future** via Association from **$IteratorDevice** (Result: **$Price_Future**)**
   │ 4. **Add **$$Price_Future
** to/from list **$PriceList****
   └─ **End Loop**
5. **Commit/Save **$DeviceList** to Database**
6. **Commit/Save **$PriceList** to Database**
7. **Show Message (Information): `Submitted`**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.