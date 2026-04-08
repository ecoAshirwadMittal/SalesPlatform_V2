# Microflow Analysis: VAL_ManualRegistration_Input

### Requirements (Inputs):
- **$RegistrationWizard** (A record of type: DocumentGeneration.RegistrationWizard)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "AppUrl
provided?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "Access
token?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Decision:** "Refresh
token?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
