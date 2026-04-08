# Microflow Analysis: SUB_Authorization_GetActiveByAuthentication

### Requirements (Inputs):
- **$Authentication** (A record of type: MicrosoftGraph.Authentication)

### Execution Steps:
1. **Search the Database for **MicrosoftGraph.Authorization** using filter: { [MicrosoftGraph.Authorization_Authentication = $Authentication]
[MicrosoftGraph.Authorization_User = $currentUser]
[Successful] } (Call this list **$Authorization**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
