# Microflow Detailed Specification: ACT_CreateActivateUserURL

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$IsSignUp** = `true`**
2. **Call Microflow **ForgotPassword.GetValidUntilDateTime** (Result: **$ValidUntil**)**
3. **Create **ForgotPassword.ForgotPassword** (Result: **$NewForgotPassword**)
      - Set **EmailAddress** = `$EcoATMDirectUser/Email`
      - Set **IsSignUp** = `true`
      - Set **UserFullname** = `$EcoATMDirectUser/FirstName`
      - Set **ValidUntill** = `$ValidUntil`
      - Set **Username** = `$EcoATMDirectUser/Email`
      - Set **ForgotPasswordURL** = `'http://localhost:8080/p/ActivateUserPage/'`
      - Set **ForgotPassword_Anon_User_Access** = `$currentUser`
      - Set **ForgotPassword_Account** = `$EcoATMDirectUser`**
4. **Call Microflow **AuctionUI.GenerateActivationUIDAndURL** (Result: **$URL**)**
5. **Call Microflow **AuctionUI.ACT_UpdateActivationDate****
6. 🏁 **END:** Return `$URL`

**Final Result:** This process concludes by returning a [String] value.