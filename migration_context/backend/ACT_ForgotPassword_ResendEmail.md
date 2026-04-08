# Microflow Detailed Specification: ACT_ForgotPassword_ResendEmail

### 📥 Inputs (Parameters)
- **$ForgotPasswordHelper** (Type: ForgotPassword.ForgotPasswordHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **ForgotPassword.SF_CreateForgotPasswordRequest** (Result: **$NewForgotPasswordRequest**)**

**Final Result:** This process concludes by returning a [Void] value.