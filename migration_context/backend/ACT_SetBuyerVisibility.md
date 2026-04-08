# Microflow Detailed Specification: ACT_SetBuyerVisibility

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
2. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$BidderUserRole**)**
3. **List Operation: **Contains** on **$undefined** (Result: **$HasBidderRole**)**
4. 🔀 **DECISION:** `$HasBidderRole = true`
   ➔ **If [true]:**
      1. **Update **$EcoATMDirectUser**
      - Set **IsBuyerRole** = `true`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$EcoATMDirectUser**
      - Set **IsBuyerRole** = `false`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.