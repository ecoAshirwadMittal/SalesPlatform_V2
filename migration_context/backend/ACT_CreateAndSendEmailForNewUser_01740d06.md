# Microflow Analysis: ACT_CreateAndSendEmailForNewUser

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [(TemplateName = 'WelcomeEmailTemplate')] } (Call this list **$EmailTemplate**)**
3. **Decision:** "Email template found?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Log Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
