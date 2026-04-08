# Microflow Analysis: SUB_User_GetAllUsers

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Create List
      - Store the result in a new variable called **$UserList****
5. **Run another process: "MicrosoftGraph.GET"
      - Store the result in a new variable called **$Response****
6. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Import Xml**
8. **Run another process: "MicrosoftGraph.SUB_User_ProcessResponse"**
9. **Decision:** "Done?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Log Message**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
