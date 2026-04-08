# Microflow Analysis: DS_User_ListGroupMembers

### Requirements (Inputs):
- **$Group** (A record of type: MicrosoftGraph.Group)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Authorization_GetActive"
      - Store the result in a new variable called **$Authorization****
2. **Run another process: "MicrosoftGraph.SUB_User_ListGroupMembers"
      - Store the result in a new variable called **$UserList****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
