# Microflow Detailed Specification: SUB_HasWholesaleBuyerCodesAssigned

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$EcoATMDirectUser** (Result: **$BuyerList**)**
2. **DB Retrieve **EcoATM_BuyerManagement.BuyerCode** Filter: `[EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser = $EcoATMDirectUser and (BuyerCodeType = 'Wholesale' or BuyerCodeType = 'Data_Wipe')]` (Result: **$BuyerCodeList**)**
3. 🔀 **DECISION:** `$BuyerCodeList != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.