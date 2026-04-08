# Nanoflow: ACT_Login_ExternalUser

**Allowed Roles:** EcoATM_UserManagement.Anonymous, EcoATM_UserManagement.Administrator

## 📥 Inputs

- **$LoginCredentials** (EcoATM_UserManagement.LoginCredentials)

## ⚙️ Execution Flow

1. **Call Microflow **EcoATM_UserManagement.VAL_Login** (Result: **$isValid**)**
2. 🔀 **DECISION:** `$isValid`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `endsWith(toLowerCase($LoginCredentials/Email),'@ecoatm.com')`
         ➔ **If [false]:**
            1. **Call Microflow **EcoATM_UserManagement.VAL_AccountAlreadyExists** (Result: **$Variable**)**
            2. 🔀 **DECISION:** `$Variable`
               ➔ **If [true]:**
                  1. **Update **$LoginCredentials**
      - Set **isEmailValid** = `true`
      - Set **emailValidationMessage** = `empty`**
                  2. **Call JS Action **NanoflowCommons.SignIn** (Result: **$LoginResult**)**
                  3. 🔀 **DECISION:** `$LoginResult!=401`
                     ➔ **If [true]:**
                        1. **Update **$LoginCredentials**
      - Set **isPasswordValid** = `true`
      - Set **passwordValidationMessage** = `empty`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **Update **$LoginCredentials**
      - Set **isPasswordValid** = `false`
      - Set **passwordValidationMessage** = `'Incorrect Password'`**
                        2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Update **$LoginCredentials**
      - Set **isEmailValid** = `false`
      - Set **emailValidationMessage** = `'No account with this email'`**
                  2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call JS Action **NanoflowCommons.OpenURL** (Result: **$ReturnValueName**)**
            2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
