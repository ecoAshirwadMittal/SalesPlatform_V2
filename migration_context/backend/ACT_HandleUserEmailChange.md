# Microflow Detailed Specification: ACT_HandleUserEmailChange

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `contains(toLowerCase($EcoATMDirectUser/Email),'@ecoatm.com')`
   ➔ **If [true]:**
      1. **Update **$EcoATMDirectUser**
      - Set **IsLocalUser** = `false`**
      2. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **DB Retrieve **System.UserRole** Filter: `[(Name = 'Bidder')]` (Result: **$UserRoleLList**)**
      2. **Update **$EcoATMDirectUser**
      - Set **IsLocalUser** = `true`
      - Set **UserRoles** = `$UserRoleLList`**
      3. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.