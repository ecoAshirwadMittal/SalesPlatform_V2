# Microflow Detailed Specification: ACT_SubmitNewPassword

### 📥 Inputs (Parameters)
- **$AccountPasswordData** (Type: ForgotPassword.AccountPasswordData)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `trim($AccountPasswordData/NewPassword) != ''`
   ➔ **If [true]:**
      1. **Update **$AccountPasswordData**
      - Set **isNewPasswordValid** = `true`
      - Set **NewPasswordValidationMessage** = `empty`**
      2. 🔀 **DECISION:** `trim($AccountPasswordData/NewPassword)!=''`
         ➔ **If [true]:**
            1. **Update **$AccountPasswordData**
      - Set **isConfirmPasswordValid** = `true`
      - Set **ConfirmPasswordValidationMessage** = `empty`**
            2. 🔀 **DECISION:** `if ($AccountPasswordData/NewPassword!=empty and $AccountPasswordData/ConfirmPassword!=empty) then $AccountPasswordData/NewPassword=$AccountPasswordData/ConfirmPassword else true`
               ➔ **If [true]:**
                  1. **Update **$AccountPasswordData**
      - Set **isConfirmPasswordValid** = `true`
      - Set **ConfirmPasswordValidationMessage** = `empty`**
                  2. 🔀 **DECISION:** `$AccountPasswordData/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/HasUppercaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/isNewPasswordValid and $AccountPasswordData/isConfirmPasswordValid`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **AccountPasswordData_ForgotPassword** via Association from **$AccountPasswordData** (Result: **$ForgotPassword**)**
                        2. **Retrieve related **ForgotPassword_Account** via Association from **$ForgotPassword** (Result: **$Account**)**
                        3. **Update **$Account** (and Save to DB)
      - Set **Password** = `$AccountPasswordData/NewPassword`
      - Set **Blocked** = `false`
      - Set **BlockedSince** = `empty`**
                        4. **Delete**
                        5. **Delete**
                        6. **Close current page/popup**
                        7. **Maps to Page: **EcoATM_UserManagement.Login_ResetPassword_Confirmation****
                        8. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$AccountPasswordData**
      - Set **isConfirmPasswordValid** = `false`
      - Set **ConfirmPasswordValidationMessage** = `'Passwords don''''t match'`**
                  2. 🔀 **DECISION:** `$AccountPasswordData/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/HasUppercaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/isNewPasswordValid and $AccountPasswordData/isConfirmPasswordValid`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **AccountPasswordData_ForgotPassword** via Association from **$AccountPasswordData** (Result: **$ForgotPassword**)**
                        2. **Retrieve related **ForgotPassword_Account** via Association from **$ForgotPassword** (Result: **$Account**)**
                        3. **Update **$Account** (and Save to DB)
      - Set **Password** = `$AccountPasswordData/NewPassword`
      - Set **Blocked** = `false`
      - Set **BlockedSince** = `empty`**
                        4. **Delete**
                        5. **Delete**
                        6. **Close current page/popup**
                        7. **Maps to Page: **EcoATM_UserManagement.Login_ResetPassword_Confirmation****
                        8. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$AccountPasswordData**
      - Set **isConfirmPasswordValid** = `false`
      - Set **ConfirmPasswordValidationMessage** = `'Please enter password'`**
            2. 🔀 **DECISION:** `$AccountPasswordData/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/HasUppercaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/isNewPasswordValid and $AccountPasswordData/isConfirmPasswordValid`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Retrieve related **AccountPasswordData_ForgotPassword** via Association from **$AccountPasswordData** (Result: **$ForgotPassword**)**
                  2. **Retrieve related **ForgotPassword_Account** via Association from **$ForgotPassword** (Result: **$Account**)**
                  3. **Update **$Account** (and Save to DB)
      - Set **Password** = `$AccountPasswordData/NewPassword`
      - Set **Blocked** = `false`
      - Set **BlockedSince** = `empty`**
                  4. **Delete**
                  5. **Delete**
                  6. **Close current page/popup**
                  7. **Maps to Page: **EcoATM_UserManagement.Login_ResetPassword_Confirmation****
                  8. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$AccountPasswordData**
      - Set **isNewPasswordValid** = `false`
      - Set **NewPasswordValidationMessage** = `'Please enter password'`**
      2. 🔀 **DECISION:** `trim($AccountPasswordData/NewPassword)!=''`
         ➔ **If [true]:**
            1. **Update **$AccountPasswordData**
      - Set **isConfirmPasswordValid** = `true`
      - Set **ConfirmPasswordValidationMessage** = `empty`**
            2. 🔀 **DECISION:** `if ($AccountPasswordData/NewPassword!=empty and $AccountPasswordData/ConfirmPassword!=empty) then $AccountPasswordData/NewPassword=$AccountPasswordData/ConfirmPassword else true`
               ➔ **If [true]:**
                  1. **Update **$AccountPasswordData**
      - Set **isConfirmPasswordValid** = `true`
      - Set **ConfirmPasswordValidationMessage** = `empty`**
                  2. 🔀 **DECISION:** `$AccountPasswordData/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/HasUppercaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/isNewPasswordValid and $AccountPasswordData/isConfirmPasswordValid`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **AccountPasswordData_ForgotPassword** via Association from **$AccountPasswordData** (Result: **$ForgotPassword**)**
                        2. **Retrieve related **ForgotPassword_Account** via Association from **$ForgotPassword** (Result: **$Account**)**
                        3. **Update **$Account** (and Save to DB)
      - Set **Password** = `$AccountPasswordData/NewPassword`
      - Set **Blocked** = `false`
      - Set **BlockedSince** = `empty`**
                        4. **Delete**
                        5. **Delete**
                        6. **Close current page/popup**
                        7. **Maps to Page: **EcoATM_UserManagement.Login_ResetPassword_Confirmation****
                        8. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$AccountPasswordData**
      - Set **isConfirmPasswordValid** = `false`
      - Set **ConfirmPasswordValidationMessage** = `'Passwords don''''t match'`**
                  2. 🔀 **DECISION:** `$AccountPasswordData/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/HasUppercaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/isNewPasswordValid and $AccountPasswordData/isConfirmPasswordValid`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Retrieve related **AccountPasswordData_ForgotPassword** via Association from **$AccountPasswordData** (Result: **$ForgotPassword**)**
                        2. **Retrieve related **ForgotPassword_Account** via Association from **$ForgotPassword** (Result: **$Account**)**
                        3. **Update **$Account** (and Save to DB)
      - Set **Password** = `$AccountPasswordData/NewPassword`
      - Set **Blocked** = `false`
      - Set **BlockedSince** = `empty`**
                        4. **Delete**
                        5. **Delete**
                        6. **Close current page/popup**
                        7. **Maps to Page: **EcoATM_UserManagement.Login_ResetPassword_Confirmation****
                        8. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Update **$AccountPasswordData**
      - Set **isConfirmPasswordValid** = `false`
      - Set **ConfirmPasswordValidationMessage** = `'Please enter password'`**
            2. 🔀 **DECISION:** `$AccountPasswordData/HasSpecialCharacter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/HasUppercaseLetter=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/IsLengthValid=AuctionUI.ENUM_PasswordStatus.Valid and $AccountPasswordData/isNewPasswordValid and $AccountPasswordData/isConfirmPasswordValid`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Retrieve related **AccountPasswordData_ForgotPassword** via Association from **$AccountPasswordData** (Result: **$ForgotPassword**)**
                  2. **Retrieve related **ForgotPassword_Account** via Association from **$ForgotPassword** (Result: **$Account**)**
                  3. **Update **$Account** (and Save to DB)
      - Set **Password** = `$AccountPasswordData/NewPassword`
      - Set **Blocked** = `false`
      - Set **BlockedSince** = `empty`**
                  4. **Delete**
                  5. **Delete**
                  6. **Close current page/popup**
                  7. **Maps to Page: **EcoATM_UserManagement.Login_ResetPassword_Confirmation****
                  8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.