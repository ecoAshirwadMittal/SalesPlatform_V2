# Microflow Analysis: DS_User_MyPhoto

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_User_GetMyPhoto"
      - Store the result in a new variable called **$MyPhoto****
2. **Decision:** "Check if "MyPhoto" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.ProfilePhoto_User] to: "$currentUser"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
