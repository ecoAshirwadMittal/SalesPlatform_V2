# Microflow Analysis: SUB_User_Update

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)
- **$User** (A record of type: MicrosoftGraph.User)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Log Message**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
