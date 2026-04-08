# Microflow Analysis: ACT_EmailAccount_RetrieveEmails

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Increment Counter Meter**
2. **Decision:** "incoming configured"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "Email_Connector.SUB_RetrieveEmails"
      - Store the result in a new variable called **$mxEmailMessages**** ⚠️ *(This step has a safety catch if it fails)*
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
