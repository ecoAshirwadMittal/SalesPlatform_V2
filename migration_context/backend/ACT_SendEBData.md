# Microflow Detailed Specification: ACT_SendEBData

### 📥 Inputs (Parameters)
- **$DAWeek** (Type: EcoATM_DA.DAWeek)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **DAWeek_Week** via Association from **$DAWeek** (Result: **$Week**)**
2. **Create Variable **$WeekNumber** = `$Week/WeekNumber`**
3. **Create Variable **$Year** = `$Week/Year`**
4. **Retrieve related **DeviceAllocation_DAWeek** via Association from **$DAWeek** (Result: **$DeviceAllocationList**)**
5. 🔀 **DECISION:** `$DeviceAllocationList!=empty`
   ➔ **If [true]:**
      1. **Retrieve related **DeviceBuyer_DAWeek** via Association from **$DAWeek** (Result: **$DeviceBuyerList**)**
      2. 🔀 **DECISION:** `$DeviceBuyerList!=empty`
         ➔ **If [true]:**
            1. **Create **Integration.Root** (Result: **$NewRoot**)**
            2. 🔄 **LOOP:** For each **$IteratorDeviceAllocation** in **$DeviceAllocationList**
               │ 1. 🔀 **DECISION:** `$IteratorDeviceAllocation/IsChanged`
               │    ➔ **If [true]:**
               │       1. **Create **Integration.JsonObject** (Result: **$NewJsonObject**)
      - Set **Week** = `$WeekNumber`
      - Set **Year** = `$Year`
      - Set **EB** = `$IteratorDeviceAllocation/EB`
      - Set **ProductID** = `toString($IteratorDeviceAllocation/ProductID)`
      - Set **Grade** = `$IteratorDeviceAllocation/Grade`
      - Set **JsonObject_Root** = `$NewRoot`**
               │       2. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/Accept=false` (Result: **$NewDeviceBuyerList**)**
               │       3. **Create **Integration.Buyercode** (Result: **$NewBuyercode**)
      - Set **Buyercode_JsonObject** = `$NewJsonObject`**
               │       4. 🔄 **LOOP:** For each **$IteratorDeviceBuyer** in **$DeviceBuyerList**
               │          │ 1. **Create **Integration.BuyercodeItem** (Result: **$NewBuyercodeItem**)
      - Set **Value** = `$IteratorDeviceBuyer/BuyerCode`
      - Set **BuyercodeItem_Buyercode** = `$NewBuyercode`**
               │          └─ **End Loop**
               │    ➔ **If [false]:**
               └─ **End Loop**
            3. **ExportXml**
            4. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.