# Microflow Detailed Specification: Val_CheckDisablingBuyer

### 📥 Inputs (Parameters)
- **$Buyer** (Type: EcoATM_BuyerManagement.Buyer)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Buyer/Status = AuctionUI.enum_BuyerStatus.Disabled`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(EcoATM_UserManagement.EcoATMDirectUser_Buyer= $Buyer)] [UserStatus = 'Active']` (Result: **$BuyerAssignmentList**)**
      2. **AggregateList**
      3. 🔀 **DECISION:** `$BuyerAssignmentCount = 0`
         ➔ **If [true]:**
            1. **Update **$Buyer**
      - Set **isFailedBuyerDisable** = `false`**
            2. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Update **$Buyer**
      - Set **Status** = `AuctionUI.enum_BuyerStatus.Active`
      - Set **isFailedBuyerDisable** = `true`**
            2. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.