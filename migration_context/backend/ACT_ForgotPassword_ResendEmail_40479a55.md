# Microflow Analysis: ACT_ForgotPassword_ResendEmail

### Requirements (Inputs):
- **$ForgotPasswordHelper** (A record of type: ForgotPassword.ForgotPasswordHelper)

### Execution Steps:
1. **Run another process: "ForgotPassword.SF_CreateForgotPasswordRequest"
      - Store the result in a new variable called **$NewForgotPasswordRequest****
2. **Close Form**
3. **Run another process: "ForgotPassword.SF_CreateAndSendEmailForReset"
      - Store the result in a new variable called **$EmailSuccesfullySent****
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
