# Microflow Detailed Specification: ACT_SaveNewUser

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.VAL_EcoAtmUser** (Result: **$IsValid**)**
2. 🔀 **DECISION:** `$IsValid = true`
   ➔ **If [true]:**
      1. **DB Retrieve **System.Language** Filter: `[Code = 'en_US']` (Result: **$Language**)**
      2. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
      3. **Call Microflow **AuctionUI.ACT_CreateAndSendEmailForNewUser** (Result: **$Variable**)**
      4. **Call Microflow **AuctionUI.ACT_SaveUserRolesAndBuyerDisplay****
      5. **Update **$EcoATMDirectUser** (and Save to DB)
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
      - Set **User_Language** = `$Language`**
      6. **LogMessage**
      7. **CreateList**
      8. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
      9. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
      10. 🔀 **DECISION:** `$EcoATMDirectUser/UserRolesDisplay ='Bidder'`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_UserManagement.SUB_SendBuyerUserToSnowflake****
            2. **Close current page/popup**
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Close current page/popup**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.