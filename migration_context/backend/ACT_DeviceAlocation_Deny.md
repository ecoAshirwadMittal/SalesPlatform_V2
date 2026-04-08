# Microflow Detailed Specification: ACT_DeviceAlocation_Deny

### 📥 Inputs (Parameters)
- **$DeviceBuyer** (Type: EcoATM_DA.DeviceBuyer)
- **$NPE_ClearingBid** (Type: EcoATM_DA.NPE_ClearingBid)
- **$DeviceAllocation** (Type: EcoATM_DA.DeviceAllocation)
- **$DAHelper** (Type: EcoATM_DA.DAHelper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$DeviceBuyer/RejectReason != empty and length(trim($DeviceBuyer/RejectReason)) > 0`
   ➔ **If [true]:**
      1. **Update **$DeviceBuyer**
      - Set **IsChanged** = `true`
      - Set **Reject** = `true`**
      2. **Update **$DeviceAllocation**
      - Set **IsChanged** = `true`**
      3. **Call Microflow **EcoATM_DA.DeviceAllocation_Save****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.