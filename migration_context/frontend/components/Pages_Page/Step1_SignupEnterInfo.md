# Page: Step1_SignupEnterInfo

**Layout:** `ForgotPassword.xAnonymousUserLayout`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Please enter your information below and we will send you a confirmation email. After confirming your account you can set your own password."
  - ⚡ **Button**: Sign-up [Style: Primary] [Class: `btn-signup`]
    ↳ [acti] → **Microflow**: `ForgotPassword.MB_Step2_SendConfirmationEmail`
