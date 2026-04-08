# Microflow Analysis: DS_Group_GetAllGroups

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Group_GetAll"
      - Store the result in a new variable called **$GroupList****
2. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
