# Microflow Analysis: ACT_UpdateActivationDate

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Decision:** "First Invite?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.EcoATMDirectUser.InvitedDate] to: "[%CurrentDateTime%]"**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.EcoATMDirectUser.LastInviteSent] to: "[%CurrentDateTime%]"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
