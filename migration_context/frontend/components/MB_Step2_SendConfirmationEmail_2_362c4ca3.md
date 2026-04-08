# Microflow Analysis: MB_Step2_SendConfirmationEmail_2

### Requirements (Inputs):
- **$SignupHelper** (A record of type: ForgotPassword.SignupHelper)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [ForgotPassword.SignupHelper.EmailAddress] to: "if $SignupHelper/EmailAddress != empty
then  toLowerCase( $SignupHelper/EmailAddress )
else ''"**
2. **Run another process: "ForgotPassword.SF_CheckForDuplicateAccount"
      - Store the result in a new variable called **$IsNewAccount****
3. **Decision:** "New?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Run another process: "ForgotPassword.UserAccountValidation"
      - Store the result in a new variable called **$Valid****
5. **Decision:** "valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Run another process: "ForgotPassword.GetValidUntilDateTime"
      - Store the result in a new variable called **$ValidUntil****
7. **Create Object
      - Store the result in a new variable called **$ForgotPassword_signup****
8. **Run another process: "ForgotPassword.GenerateUIDAndURL"**
9. **Run another process: "ForgotPassword.SF_CreateAndSendEmailForSignup"
      - Store the result in a new variable called **$EmailSuccesfullySent****
10. **Close Form**
11. **Decision:** "Succes?"
   - If [true] -> Move to: **is new account?**
   - If [false] -> Move to: **Activity**
12. **Decision:** "is new account?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
13. **Show Page**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
