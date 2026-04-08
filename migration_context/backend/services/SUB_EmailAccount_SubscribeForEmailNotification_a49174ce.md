# Microflow Analysis: SUB_EmailAccount_SubscribeForEmailNotification

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName**** ⚠️ *(This step has a safety catch if it fails)*
2. **Decision:** "NotifyOnNewEmails"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName**** ⚠️ *(This step has a safety catch if it fails)*
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
