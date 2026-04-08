# Microflow Analysis: SUB_Authorization_GetOrCreate

### Requirements (Inputs):
- **$Authentication** (A record of type: MicrosoftGraph.Authentication)

### Execution Steps:
1. **Search the Database for **MicrosoftGraph.Authorization** using filter: { [MicrosoftGraph.Authorization_Authentication = $Authentication]
[MicrosoftGraph.Authorization_User = $currentUser] } (Call this list **$Authorization**)**
2. **Java Action Call
      - Store the result in a new variable called **$State****
3. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authorization.State] to: "$State"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
