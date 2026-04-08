# Microflow Analysis: SUB_Group_Delete

### Requirements (Inputs):
- **$GroupId** (A record of type: Object)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Log Message**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
