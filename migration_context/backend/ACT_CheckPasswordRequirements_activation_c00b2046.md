# Microflow Analysis: ACT_CheckPasswordRequirements_activation

### Requirements (Inputs):
- **$ActivateUser** (A record of type: EcoATM_UserManagement.ActivateUser)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Create Variable**
4. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.ActivateUser.IsLengthValid] to: "$length"
      - Change [EcoATM_UserManagement.ActivateUser.HasUpperCaseLetter] to: "$upper"
      - Change [EcoATM_UserManagement.ActivateUser.HasSpecialCharacter] to: "$special"
      - **Save:** This change will be saved to the database immediately.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
