# Microflow Detailed Specification: SUB_CreateNewUserFromExisting

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SUB_CreateNewUserFromExisting'`**
2. **Create Variable **$Description** = `'Create a new user based on an existing user'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create **EcoATM_UserManagement.EcoATMDirectUser** (Result: **$NewEcoATMDirectUser**)**
5. **Update **$NewEcoATMDirectUser**
      - Set **FirstName** = `$EcoATMDirectUser/FirstName`
      - Set **LastName** = `$EcoATMDirectUser/LastName`
      - Set **FullName** = `$EcoATMDirectUser/FirstName + ' ' + $EcoATMDirectUser/LastName`
      - Set **Email** = `toLowerCase($EcoATMDirectUser/Email)`
      - Set **Password** = `'EcoAtm@022024'`
      - Set **IsLocalUser** = `if contains($EcoATMDirectUser/Email, 'ecoatm.com') then false else true`
      - Set **Name** = `$EcoATMDirectUser/Email`
      - Set **UserRoles** = `$EcoATMDirectUser/System.UserRoles`
      - Set **EcoATMDirectUser_Buyer** = `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer`
      - Set **Inactive** = `true`
      - Set **EcoATMDirectUser_Buyer** = `$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer`**
6. **Call Microflow **AuctionUI.ACT_SaveNewUser****
7. **Delete**
8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.