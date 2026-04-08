# Microflow Analysis: SUB_SetUserOwnerAndChanger

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Decision:** "new user?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.EcoATMDirectUser.EntityOwner] to: "$currentUser/Name"
      - Change [EcoATM_UserManagement.EcoATMDirectUser.EntityChanger] to: "$currentUser/Name"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
