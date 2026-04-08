# Page: Step1_ForgotPassword

**Layout:** `Atlas_Core.Atlas_Default`

## Widget Tree

- 📦 **DataView** [Context]
  - 🔤 **Text**: "Don't worry; just enter your email address and we will send you an email to update your password." [Class: `pageheader-subtitle loginsubtitle`]
  - ⚡ **Button**: Recover Password [Style: Default] [Class: `btn-recover`]
    ↳ [acti] → **Microflow**: `ForgotPassword.Step2_SendEmailRecoverPassword`
