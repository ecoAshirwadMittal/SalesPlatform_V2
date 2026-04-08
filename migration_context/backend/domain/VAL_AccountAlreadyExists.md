# Microflow Detailed Specification: VAL_AccountAlreadyExists

### 📥 Inputs (Parameters)
- **$LoginCredentials** (Type: EcoATM_UserManagement.LoginCredentials)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email=$LoginCredentials/Email) and Active = true]` (Result: **$EcoATMDirectUser**)**
2. 🔀 **DECISION:** `$EcoATMDirectUser != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.