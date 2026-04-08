# Microflow Analysis: SUB_Authorization_GetActive

### Requirements (Inputs):
- **$User** (A record of type: System.User)

### Execution Steps:
1. **Search the Database for **MicrosoftGraph.Authorization** using filter: { [MicrosoftGraph.Authorization_Authentication/MicrosoftGraph.Authentication/IsActive]
[MicrosoftGraph.Authorization_User = $User]
[Successful] } (Call this list **$Authorization**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
