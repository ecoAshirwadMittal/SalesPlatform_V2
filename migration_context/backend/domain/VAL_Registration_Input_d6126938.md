# Microflow Analysis: VAL_Registration_Input

### Requirements (Inputs):
- **$RegistrationWizard** (A record of type: DocumentGeneration.RegistrationWizard)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Deployment
type?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "PAT
provided?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Decision:** "AppUrl
provided?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Decision:** "AppId provided?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
