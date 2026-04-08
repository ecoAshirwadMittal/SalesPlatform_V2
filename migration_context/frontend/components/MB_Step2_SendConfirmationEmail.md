# Microflow Detailed Specification: MB_Step2_SendConfirmationEmail

### 📥 Inputs (Parameters)
- **$SignupHelper** (Type: ForgotPassword.SignupHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Update **$SignupHelper**
      - Set **EmailAddress** = `if $SignupHelper/EmailAddress != empty then toLowerCase( $SignupHelper/EmailAddress ) else ''`**
2. **Call Microflow **ForgotPassword.SF_CheckForDuplicateAccount** (Result: **$IsNewAccount**)**
3. 🔀 **DECISION:** `$IsNewAccount`
   ➔ **If [true]:**
      1. **Call Microflow **ForgotPassword.UserAccountValidation** (Result: **$Valid**)**
      2. 🔀 **DECISION:** `$Valid`
         ➔ **If [true]:**
            1. **Call Microflow **ForgotPassword.GetValidUntilDateTime** (Result: **$ValidUntil**)**
            2. **Create **ForgotPassword.ForgotPassword** (Result: **$ForgotPassword_signup**)
      - Set **ForgotPassword_Anon_User_Access** = `$currentUser`
      - Set **IsSignUp** = `true`
      - Set **ValidUntill** = `$ValidUntil`
      - Set **EmailAddress** = `$SignupHelper/EmailAddress`
      - Set **Username** = `$SignupHelper/EmailAddress`
      - Set **UserFullname** = `$SignupHelper/UserFullname`**
            3. **Call Microflow **ForgotPassword.GenerateUIDAndURL****
            4. **Call Microflow **ForgotPassword.SF_CreateAndSendEmailForSignup** (Result: **$EmailSuccesfullySent**)**
            5. **Close current page/popup**
            6. 🔀 **DECISION:** `$EmailSuccesfullySent`
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$IsNewAccount`
                     ➔ **If [false]:**
                        1. **Maps to Page: **ForgotPassword.Step2_ConfirmationMessageReset****
                        2. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Maps to Page: **ForgotPassword.Step2_ConfirmationMessageSignup****
                        2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **Maps to Page: **ForgotPassword.Step2_FailedMessage****
                  2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **ValidationFeedback**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.