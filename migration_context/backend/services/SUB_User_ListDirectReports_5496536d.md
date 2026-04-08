# Microflow Analysis: SUB_User_ListDirectReports

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)
- **$UserIdOrUserPrincipalName_Optional** (A record of type: Object)
- **$Parameters_Optional** (A record of type: Object)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Create Variable**
4. **Decision:** "Check if "UserIdOrUserPrincipalName_Optional" is not empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Check if "$Parameters_Optional" is not empty**
5. **Change Variable**
6. **Decision:** "Check if "$Parameters_Optional" is not empty"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
7. **Change Variable**
8. **Run another process: "MicrosoftGraph.GET"
      - Store the result in a new variable called **$Response****
9. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Import Xml**
11. **Retrieve
      - Store the result in a new variable called **$Value****
12. **Retrieve
      - Store the result in a new variable called **$UserList****
13. **Log Message**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
