# Microflow Analysis: SUB_Authorization_GetAccessToken_AuthorizationCode

### Requirements (Inputs):
- **$Code** (A record of type: Object)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Decision:** "Check if "Code" exists"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
2. **Log Message**
3. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authorization.Successful] to: "false"**
4. **Log Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
