# Microflow Detailed Specification: ACT_CreateAndSendEmailForNewUser

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$IsSignUp** = `true`**
2. **DB Retrieve **Email_Connector.EmailTemplate** Filter: `[(TemplateName = 'WelcomeEmailTemplate')]` (Result: **$EmailTemplate**)**
3. 🔀 **DECISION:** `$EmailTemplate != empty`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🏁 **END:** Return `false`
   ➔ **If [true]:**
      1. **Call Microflow **ForgotPassword.GetValidUntilDateTime** (Result: **$ValidUntil**)**
      2. **Create **ForgotPassword.ForgotPassword** (Result: **$NewForgotPassword**)
      - Set **EmailAddress** = `$EcoATMDirectUser/Email`
      - Set **IsSignUp** = `true`
      - Set **UserFullname** = `$EcoATMDirectUser/FirstName`
      - Set **ValidUntill** = `$ValidUntil`
      - Set **Username** = `$EcoATMDirectUser/Email`
      - Set **ForgotPasswordURL** = `'http://localhost:8080/p/ActivateUserPage/'`
      - Set **ForgotPassword_Anon_User_Access** = `$currentUser`
      - Set **ForgotPassword_Account** = `$EcoATMDirectUser`**
      3. **Call Microflow **AuctionUI.GenerateActivationUIDAndURL** (Result: **$Variable**)**
      4. **Call Microflow **AuctionUI.ACT_CreateAndSendEmail** (Result: **$IsEmailSent**)**
      5. **Call Microflow **AuctionUI.ACT_UpdateActivationDate****
      6. 🏁 **END:** Return `$IsEmailSent`

**Final Result:** This process concludes by returning a [Boolean] value.