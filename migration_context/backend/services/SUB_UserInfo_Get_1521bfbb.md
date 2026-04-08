# Microflow Analysis: SUB_UserInfo_Get

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "MicrosoftGraph.GET"
      - Store the result in a new variable called **$Response****
3. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Import Xml**
5. **Log Message**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
