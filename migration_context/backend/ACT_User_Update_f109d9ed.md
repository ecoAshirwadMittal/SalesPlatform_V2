# Microflow Analysis: ACT_User_Update

### Requirements (Inputs):
- **$User** (A record of type: MicrosoftGraph.User)

### Execution Steps:
1. **Search the Database for **MicrosoftGraph.Authorization** using filter: { [MicrosoftGraph.Authorization_User = $currentUser] } (Call this list **$Authorization**)**
2. **Run another process: "MicrosoftGraph.SUB_User_Update"
      - Store the result in a new variable called **$Updated****
3. **Decision:** "Check if "$Updated" is true"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Close Form**
5. **Update the **$undefined** (Object):**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
