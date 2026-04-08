# Microflow Analysis: ACT_ActivateNewAccount

### Requirements (Inputs):
- **$ActivateUser** (A record of type: EcoATM_UserManagement.ActivateUser)

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_CheckPasswordRequirements_activation"**
2. **Decision:** "pass rules?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Validation Feedback**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
