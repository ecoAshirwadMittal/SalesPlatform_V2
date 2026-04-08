# Microflow Analysis: ACT_ToggleUserActive

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Active?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Change Variable**
4. **Update the **$undefined** (Object):
      - Change [System.User.Active] to: "$newActiveValue"
      - **Save:** This change will be saved to the database immediately.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
