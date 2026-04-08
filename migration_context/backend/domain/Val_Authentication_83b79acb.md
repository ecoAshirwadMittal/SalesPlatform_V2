# Microflow Analysis: Val_Authentication

### Requirements (Inputs):
- **$Authentication** (A record of type: MicrosoftGraph.Authentication)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "Check if "Authentication" exists"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "Check if "$Authentication/AppId" is not empty"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Decision:** "Check if "$Authentication/Tenant_Id" is not empty"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Decision:** "Check if "$Authentication/Authority" is not empty"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Decision:** "Check if "$Authentication/client_secret" is not empty"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
7. **Decision:** "Check based on "$Authentication/Prompt""
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
8. **Decision:** "Check if "$Authentication/MicrosoftGraph.SelectedResponseMode" is not empty"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
9. **Decision:** "Check if "$Authentication/MicrosoftGraph.SelectedResponseType" is not empty"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
