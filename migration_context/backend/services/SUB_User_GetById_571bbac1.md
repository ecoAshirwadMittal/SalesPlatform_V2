# Microflow Analysis: SUB_User_GetById

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)
- **$UserId** (A record of type: Object)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Run another process: "MicrosoftGraph.GET"
      - Store the result in a new variable called **$Response****
5. **Decision:** "Successful?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Import Xml**
7. **Log Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
