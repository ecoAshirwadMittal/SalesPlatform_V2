# Microflow Analysis: VAL_Login

### Requirements (Inputs):
- **$LoginCredentials** (A record of type: EcoATM_UserManagement.LoginCredentials)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.LoginCredentials.isEmailValid] to: "true"
      - Change [EcoATM_UserManagement.LoginCredentials.emailValidationMessage] to: "empty"
      - Change [EcoATM_UserManagement.LoginCredentials.isPasswordValid] to: "true"
      - Change [EcoATM_UserManagement.LoginCredentials.passwordValidationMessage] to: "empty"**
2. **Create Variable**
3. **Decision:** "email not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.LoginCredentials.isEmailValid] to: "true"
      - Change [EcoATM_UserManagement.LoginCredentials.emailValidationMessage] to: "empty"**
5. **Decision:** "ecoatm email?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **password not empty?**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
