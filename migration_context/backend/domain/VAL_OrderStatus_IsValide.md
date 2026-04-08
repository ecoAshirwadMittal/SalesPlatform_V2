# Microflow Detailed Specification: VAL_OrderStatus_IsValide

### 📥 Inputs (Parameters)
- **$OrderStatus** (Type: EcoATM_PWS.OrderStatus)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.OrderStatus** Filter: `[SystemStatus=$OrderStatus/SystemStatus] [id!=$OrderStatus]` (Result: **$AltreadyExistOrderStatus**)**
2. 🔀 **DECISION:** `$AltreadyExistOrderStatus=empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.