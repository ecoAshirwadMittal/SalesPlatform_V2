# Microflow Detailed Specification: SUB_GetChangedDeviceAllocation

### 📥 Inputs (Parameters)
- **$DeviceBuyerList** (Type: EcoATM_DA.DeviceBuyer)

### ⚙️ Execution Flow (Logic Steps)
1. **CreateList**
2. 🔄 **LOOP:** For each **$IteratorDeviceBuyer** in **$DeviceBuyerList**
   │ 1. **Retrieve related **DeviceBuyer_DeviceAllocation** via Association from **$IteratorDeviceBuyer** (Result: **$DeviceAllocation**)**
   │ 2. **Add **$$DeviceAllocation** to/from list **$DeviceAllocationList****
   └─ **End Loop**
3. 🏁 **END:** Return `$DeviceAllocationList`

**Final Result:** This process concludes by returning a [List] value.