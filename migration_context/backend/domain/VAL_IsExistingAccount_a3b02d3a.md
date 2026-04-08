# Microflow Analysis: VAL_IsExistingAccount

### Requirements (Inputs):
- **$LoginCredentials** (A record of type: EcoATM_UserManagement.LoginCredentials)

### Execution Steps:
1. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [(contains(Email,$LoginCredentials/Email)) and Active = true] } (Call this list **$EcoATMDirectUser**)**
2. **Decision:** "Exists?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
