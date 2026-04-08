# Microflow Analysis: ACT_SendPasswordResetEmail

### Requirements (Inputs):
- **$ForgotPasswordHelper** (A record of type: ForgotPassword.ForgotPasswordHelper)

### Execution Steps:
1. **Decision:** "email not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [ForgotPassword.ForgotPasswordHelper.emailValidationMessage] to: "empty"
      - Change [ForgotPassword.ForgotPasswordHelper.isEmailValid] to: "true"**
3. **Run another process: "ForgotPassword.SF_CreateForgotPasswordRequest"
      - Store the result in a new variable called **$NewForgotPasswordRequest****
4. **Close Form**
5. **Run another process: "ForgotPassword.SF_CreateAndSendEmailForReset"
      - Store the result in a new variable called **$EmailSuccesfullySent****
6. **Show Page**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
