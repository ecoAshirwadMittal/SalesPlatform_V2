# Microflow Detailed Specification: VAL_Login

### 📥 Inputs (Parameters)
- **$LoginCredentials** (Type: EcoATM_UserManagement.LoginCredentials)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$LoginCredentials**
      - Set **isEmailValid** = `true`
      - Set **emailValidationMessage** = `empty`
      - Set **isPasswordValid** = `true`
      - Set **passwordValidationMessage** = `empty`**
2. **Create Variable **$isValid** = `true`**
3. 🔀 **DECISION:** `$LoginCredentials/Email!=empty`
   ➔ **If [true]:**
      1. **Update **$LoginCredentials**
      - Set **isEmailValid** = `true`
      - Set **emailValidationMessage** = `empty`**
      2. 🔀 **DECISION:** `endsWith(toLowerCase($LoginCredentials/Email),'@ecoatm.com')`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$LoginCredentials/Password!=empty`
               ➔ **If [true]:**
                  1. **Update **$LoginCredentials**
      - Set **isPasswordValid** = `true`
      - Set **passwordValidationMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **Update **$LoginCredentials**
      - Set **isPasswordValid** = `false`
      - Set **passwordValidationMessage** = `'Please enter password'`**
                  3. 🏁 **END:** Return `$isValid`
   ➔ **If [false]:**
      1. **Update Variable **$isValid** = `false`**
      2. **Update **$LoginCredentials**
      - Set **isEmailValid** = `false`
      - Set **emailValidationMessage** = `'Please enter email'`**
      3. 🔀 **DECISION:** `endsWith(toLowerCase($LoginCredentials/Email),'@ecoatm.com')`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$isValid`
         ➔ **If [false]:**
            1. 🔀 **DECISION:** `$LoginCredentials/Password!=empty`
               ➔ **If [true]:**
                  1. **Update **$LoginCredentials**
      - Set **isPasswordValid** = `true`
      - Set **passwordValidationMessage** = `empty`**
                  2. 🏁 **END:** Return `$isValid`
               ➔ **If [false]:**
                  1. **Update Variable **$isValid** = `false`**
                  2. **Update **$LoginCredentials**
      - Set **isPasswordValid** = `false`
      - Set **passwordValidationMessage** = `'Please enter password'`**
                  3. 🏁 **END:** Return `$isValid`

**Final Result:** This process concludes by returning a [Boolean] value.