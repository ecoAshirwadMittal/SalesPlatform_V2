# Microflow Analysis: ACT_Group_View

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)
- **$Group** (A record of type: MicrosoftGraph.Group)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Group_GetById"
      - Store the result in a new variable called **$Group_Retrieved****
2. **Show Page**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
