# Microflow Detailed Specification: ACT_ActivateNewUser

### 📥 Inputs (Parameters)
- **$ActivateUser** (Type: EcoATM_UserManagement.ActivateUser)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($ActivateUser/Password) != ''`
   ➔ **If [true]:**
      1. **Update **$ActivateUser**
      - Set **isNewPasswordValid** = `true`
      - Set **NewPasswordValidationMessage** = `empty`**
      2. 🔀 **DECISION:** `trim($ActivateUser/ConfirmPassword)!=''`
         ➔ **If [true]:**
            1. **Update **$ActivateUser**
      - Set **isConfirmPasswordValid** = `true`
      - Set **ConfirmPasswordValidationMessage** = `empty`**
            2. 🔀 **DECISION:** `if ($ActivateUser/Password!=empty and $ActivateUser/ConfirmPassword!=empty) then trim($ActivateUser/Password)=trim($ActivateUser/ConfirmPassword) else true`
               ➔ **If [true]:**
                  1. **Update **$ActivateUser**
      - Set **isConfirmPasswordValid** = `true`
      - Set **ConfirmPasswordValidationMessage** = `empty`**
                  2. 🔀 **DECISION:** `$ActivateUser/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/HasUpperCaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/isNewPasswordValid and $ActivateUser/isConfirmPasswordValid`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email = $ActivateUser/email)]` (Result: **$EcoATMDirectUser**)**
                        2. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                        3. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                        4. **CreateList**
                        5. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                        6. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                        7. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                        8. **Delete**
                        9. **Maps to Page: **EcoATM_UserManagement.Login_AccountActivated****
                        10. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Update **$ActivateUser**
      - Set **isConfirmPasswordValid** = `false`
      - Set **ConfirmPasswordValidationMessage** = `'Passwords don''''t match'`**
                  2. 🔀 **DECISION:** `$ActivateUser/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/HasUpperCaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/isNewPasswordValid and $ActivateUser/isConfirmPasswordValid`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email = $ActivateUser/email)]` (Result: **$EcoATMDirectUser**)**
                        2. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                        3. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                        4. **CreateList**
                        5. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                        6. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                        7. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                        8. **Delete**
                        9. **Maps to Page: **EcoATM_UserManagement.Login_AccountActivated****
                        10. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Update **$ActivateUser**
      - Set **isConfirmPasswordValid** = `false`
      - Set **ConfirmPasswordValidationMessage** = `'Please enter password'`**
            2. 🔀 **DECISION:** `$ActivateUser/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/HasUpperCaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/isNewPasswordValid and $ActivateUser/isConfirmPasswordValid`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email = $ActivateUser/email)]` (Result: **$EcoATMDirectUser**)**
                  2. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                  3. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                  4. **CreateList**
                  5. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                  6. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                  7. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                  8. **Delete**
                  9. **Maps to Page: **EcoATM_UserManagement.Login_AccountActivated****
                  10. 🏁 **END:** Return `true`
   ➔ **If [false]:**
      1. **Update **$ActivateUser**
      - Set **isNewPasswordValid** = `false`
      - Set **NewPasswordValidationMessage** = `'Please enter password'`**
      2. 🔀 **DECISION:** `trim($ActivateUser/ConfirmPassword)!=''`
         ➔ **If [true]:**
            1. **Update **$ActivateUser**
      - Set **isConfirmPasswordValid** = `true`
      - Set **ConfirmPasswordValidationMessage** = `empty`**
            2. 🔀 **DECISION:** `if ($ActivateUser/Password!=empty and $ActivateUser/ConfirmPassword!=empty) then trim($ActivateUser/Password)=trim($ActivateUser/ConfirmPassword) else true`
               ➔ **If [true]:**
                  1. **Update **$ActivateUser**
      - Set **isConfirmPasswordValid** = `true`
      - Set **ConfirmPasswordValidationMessage** = `empty`**
                  2. 🔀 **DECISION:** `$ActivateUser/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/HasUpperCaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/isNewPasswordValid and $ActivateUser/isConfirmPasswordValid`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email = $ActivateUser/email)]` (Result: **$EcoATMDirectUser**)**
                        2. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                        3. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                        4. **CreateList**
                        5. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                        6. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                        7. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                        8. **Delete**
                        9. **Maps to Page: **EcoATM_UserManagement.Login_AccountActivated****
                        10. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. **Update **$ActivateUser**
      - Set **isConfirmPasswordValid** = `false`
      - Set **ConfirmPasswordValidationMessage** = `'Passwords don''''t match'`**
                  2. 🔀 **DECISION:** `$ActivateUser/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/HasUpperCaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/isNewPasswordValid and $ActivateUser/isConfirmPasswordValid`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email = $ActivateUser/email)]` (Result: **$EcoATMDirectUser**)**
                        2. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                        3. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                        4. **CreateList**
                        5. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                        6. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                        7. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                        8. **Delete**
                        9. **Maps to Page: **EcoATM_UserManagement.Login_AccountActivated****
                        10. 🏁 **END:** Return `true`
         ➔ **If [false]:**
            1. **Update **$ActivateUser**
      - Set **isConfirmPasswordValid** = `false`
      - Set **ConfirmPasswordValidationMessage** = `'Please enter password'`**
            2. 🔀 **DECISION:** `$ActivateUser/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/HasUpperCaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $ActivateUser/isNewPasswordValid and $ActivateUser/isConfirmPasswordValid`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email = $ActivateUser/email)]` (Result: **$EcoATMDirectUser**)**
                  2. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
                  3. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **Password** = `$ActivateUser/Password`
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`
      - Set **Inactive** = `false`
      - Set **OverallUserStatus** = `AuctionUI.enum_OverallUserStatus.Active`**
                  4. **CreateList**
                  5. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
                  6. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
                  7. **Retrieve related **ActivateUser_ForgotPassword** via Association from **$ActivateUser** (Result: **$ForgotPassword**)**
                  8. **Delete**
                  9. **Maps to Page: **EcoATM_UserManagement.Login_AccountActivated****
                  10. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Void] value.