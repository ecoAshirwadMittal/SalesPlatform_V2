# Microflow Analysis: ACT_ShowUserEditPage

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Decision:** "Bidder?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Show Page**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
