# Microflow Detailed Specification: ACT_SendPasswordResetEmail

### 📥 Inputs (Parameters)
- **$ForgotPasswordHelper** (Type: ForgotPassword.ForgotPasswordHelper)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$ForgotPasswordHelper/EmailAddress!=empty`
   ➔ **If [true]:**
      1. **Update **$ForgotPasswordHelper**
      - Set **emailValidationMessage** = `empty`
      - Set **isEmailValid** = `true`**
      2. **Call Microflow **ForgotPassword.SF_CreateForgotPasswordRequest** (Result: **$NewForgotPasswordRequest**)**
   ➔ **If [false]:**
      1. **Update **$ForgotPasswordHelper**
      - Set **emailValidationMessage** = `'Please enter email'`
      - Set **isEmailValid** = `false`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.