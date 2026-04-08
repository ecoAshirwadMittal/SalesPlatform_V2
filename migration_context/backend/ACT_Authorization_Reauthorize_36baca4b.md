# Microflow Analysis: ACT_Authorization_Reauthorize

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Authorization_Reauthorize"
      - Store the result in a new variable called **$Reauthorized****
2. **Decision:** "Check if "$Reauthorized" is true"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Admin Consent?**
3. **Update the **$undefined** (Object):**
4. **Show Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
