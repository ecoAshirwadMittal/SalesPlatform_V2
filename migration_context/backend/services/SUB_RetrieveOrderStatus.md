# Microflow Detailed Specification: SUB_RetrieveOrderStatus

### 📥 Inputs (Parameters)
- **$ENUM_PWSOrderStatus** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$StatusParam** = `getCaption($ENUM_PWSOrderStatus)`**
2. **DB Retrieve **EcoATM_PWS.OrderStatus** Filter: `[SystemStatus=$StatusParam]` (Result: **$OrderStatus**)**
3. 🏁 **END:** Return `$OrderStatus`

**Final Result:** This process concludes by returning a [Object] value.