# Microflow Detailed Specification: ACT_GenerateATPQuantity

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWSMDM.Device**  (Result: **$DeviceList**)**
2. 🔄 **LOOP:** For each **$IteratorDevice** in **$DeviceList**
   │ 1. **Update **$IteratorDevice**
      - Set **ATPQty** = `$IteratorDevice/AvailableQty`**
   └─ **End Loop**
3. **Commit/Save **$DeviceList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.