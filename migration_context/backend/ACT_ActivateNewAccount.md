# Microflow Detailed Specification: ACT_ActivateNewAccount

### 📥 Inputs (Parameters)
- **$ActivateUser** (Type: EcoATM_UserManagement.ActivateUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_CheckPasswordRequirements_activation****
2. 🔀 **DECISION:** `true`
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email = $ActivateUser/email)]` (Result: **$EcoATMDirectUser**)**
      2. **Create Variable **$ValidPassword** = `true`**
      3. 🔀 **DECISION:** `$ActivateUser/Password = $ActivateUser/ConfirmPassword`
         ➔ **If [true]:**
            1. **JavaCallAction**
            2. 🔀 **DECISION:** `$ActivateUser/Password = $ActivateUser/ConfirmPassword`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$ValidPassword = true`
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                        2. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                        3. **CreateList**
                        4. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                        5. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                        6. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                        7. **Delete**
                        8. **Create **EcoATM_UserManagement.LoginCredentials** (Result: **$NewLoginCredentials**)
      - Set **Email** = `$ActivateUser/email`**
                        9. **Maps to Page: **AuctionUI.Login_Account_Activation****
                        10. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
               ➔ **If [false]:**
                  1. **Update Variable **$ValidPassword** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$ValidPassword = true`
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                        2. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                        3. **CreateList**
                        4. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                        5. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                        6. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                        7. **Delete**
                        8. **Create **EcoATM_UserManagement.LoginCredentials** (Result: **$NewLoginCredentials**)
      - Set **Email** = `$ActivateUser/email`**
                        9. **Maps to Page: **AuctionUI.Login_Account_Activation****
                        10. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **Update Variable **$ValidPassword** = `false`**
            2. **ValidationFeedback**
            3. **JavaCallAction**
            4. 🔀 **DECISION:** `$ActivateUser/Password = $ActivateUser/ConfirmPassword`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$ValidPassword = true`
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                        2. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                        3. **CreateList**
                        4. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                        5. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                        6. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                        7. **Delete**
                        8. **Create **EcoATM_UserManagement.LoginCredentials** (Result: **$NewLoginCredentials**)
      - Set **Email** = `$ActivateUser/email`**
                        9. **Maps to Page: **AuctionUI.Login_Account_Activation****
                        10. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
               ➔ **If [false]:**
                  1. **Update Variable **$ValidPassword** = `false`**
                  2. **ValidationFeedback**
                  3. 🔀 **DECISION:** `$ValidPassword = true`
                     ➔ **If [true]:**
                        1. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                        2. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                        3. **CreateList**
                        4. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                        5. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                        6. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                        7. **Delete**
                        8. **Create **EcoATM_UserManagement.LoginCredentials** (Result: **$NewLoginCredentials**)
      - Set **Email** = `$ActivateUser/email`**
                        9. **Maps to Page: **AuctionUI.Login_Account_Activation****
                        10. 🏁 **END:** Return `true`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Void] value.