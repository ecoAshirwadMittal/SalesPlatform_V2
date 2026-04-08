# Microflow Analysis: BCO_Authorization

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Decision:** "Check if "Authorization/Refresh_Token" exists"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Check if "$Authorization/Refresh_Token" is encrypted**
2. **Decision:** "Check if "Authorization/Access_Token" exists"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Check if "$Authorization/Access_Token" is encrypted**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
