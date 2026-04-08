# Microflow Analysis: ACT_SubmitNewPassword

### Requirements (Inputs):
- **$AccountPasswordData** (A record of type: ForgotPassword.AccountPasswordData)

### Execution Steps:
1. **Decision:** "new password not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [ForgotPassword.AccountPasswordData.isNewPasswordValid] to: "true"
      - Change [ForgotPassword.AccountPasswordData.NewPasswordValidationMessage] to: "empty"**
3. **Decision:** "confirm password not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [ForgotPassword.AccountPasswordData.isConfirmPasswordValid] to: "true"
      - Change [ForgotPassword.AccountPasswordData.ConfirmPasswordValidationMessage] to: "empty"**
5. **Decision:** "passwords match?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Update the **$undefined** (Object):
      - Change [ForgotPassword.AccountPasswordData.isConfirmPasswordValid] to: "true"
      - Change [ForgotPassword.AccountPasswordData.ConfirmPasswordValidationMessage] to: "empty"**
7. **Decision:** "valid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
