# Microflow Detailed Specification: ACT_SaveUserRolesAndBuyerDisplay

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **EcoATMDirectUser_Buyer** via Association from **$EcoATMDirectUser** (Result: **$BuyerList**)**
2. **AggregateList**
3. **Retrieve related **UserRoles** via Association from **$EcoATMDirectUser** (Result: **$UserRoleList**)**
4. **AggregateList**
5. **Update **$EcoATMDirectUser**
      - Set **UserBuyerDisplay** = `$ReducedBuyerList`
      - Set **UserRolesDisplay** = `$ReduceUserRoleList`**
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.