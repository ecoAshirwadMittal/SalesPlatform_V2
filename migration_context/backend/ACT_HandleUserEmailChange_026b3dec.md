# Microflow Analysis: ACT_HandleUserEmailChange

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Decision:** "ecoAtm email?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [Administration.Account.IsLocalUser] to: "false"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
