# Microflow Analysis: SUB_Authorization_Reauthorize

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Decision:** "Check if "Authorization" exists"
   - If [true] -> Move to: **Admin Consent?**
   - If [false] -> Move to: **Activity**
2. **Decision:** "Admin Consent?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Run another process: "MicrosoftGraph.SUB_Authorization_RefreshAccessToken"
      - Store the result in a new variable called **$Successful****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
