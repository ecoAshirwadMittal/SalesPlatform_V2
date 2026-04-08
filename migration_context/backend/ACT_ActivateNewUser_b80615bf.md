# Microflow Analysis: ACT_ActivateNewUser

### Requirements (Inputs):
- **$ActivateUser** (A record of type: EcoATM_UserManagement.ActivateUser)

### Execution Steps:
1. **Decision:** "new password not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.ActivateUser.isNewPasswordValid] to: "true"
      - Change [EcoATM_UserManagement.ActivateUser.NewPasswordValidationMessage] to: "empty"**
3. **Decision:** "confirm password not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.ActivateUser.isConfirmPasswordValid] to: "true"
      - Change [EcoATM_UserManagement.ActivateUser.ConfirmPasswordValidationMessage] to: "empty"**
5. **Decision:** "passwords match?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.ActivateUser.isConfirmPasswordValid] to: "true"
      - Change [EcoATM_UserManagement.ActivateUser.ConfirmPasswordValidationMessage] to: "empty"**
7. **Decision:** "valid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
