# Microflow Analysis: SUB_User_Create

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)
- **$User** (A record of type: MicrosoftGraph.User)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Export Xml**
5. **Run another process: "MicrosoftGraph.POST"
      - Store the result in a new variable called **$Response****
6. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Log Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
