# Microflow Analysis: DS_User_DirectReports

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_User_ListDirectReports"
      - Store the result in a new variable called **$UserList****
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
