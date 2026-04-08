# Microflow Detailed Specification: ACT_SaveUserChanges

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'ACT_SaveUserChanges'`**
2. **Create Variable **$Description** = `'Save User Changes: user-'+$EcoATMDirectUser/Name`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **AuctionUI.VAL_EcoAtmUser** (Result: **$IsValid**)**
5. 🔀 **DECISION:** `$IsValid = true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.SUB_CheckChangedEmail** (Result: **$EmailChanged**)**
      2. 🔀 **DECISION:** `$EmailChanged`
         ➔ **If [false]:**
            1. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Active** = `if $EcoATMDirectUser/UserStatus = AuctionUI.enum_UserStatus.Disabled then false else true`
      - Set **OverallUserStatus** = `if $EcoATMDirectUser/Inactive = true then AuctionUI.enum_OverallUserStatus.Inactive else if $EcoATMDirectUser/UserStatus = AuctionUI.enum_UserStatus.Disabled then AuctionUI.enum_OverallUserStatus.Disabled else AuctionUI.enum_OverallUserStatus.Active`
      - Set **FullName** = `$EcoATMDirectUser/FirstName + ' ' + $EcoATMDirectUser/LastName`**
            2. **Call Microflow **AuctionUI.ACT_SaveUserRolesAndBuyerDisplay****
            3. **CreateList**
            4. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
            5. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
            6. 🔀 **DECISION:** `$EcoATMDirectUser/IsLocalUser`
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_UserManagement.SUB_SendBuyerUserToSnowflake****
                  2. **Close current page/popup**
                  3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  4. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Close current page/popup**
                  2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  3. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **DeepLink.SUB_CreateNewUserFromExisting****
            2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.