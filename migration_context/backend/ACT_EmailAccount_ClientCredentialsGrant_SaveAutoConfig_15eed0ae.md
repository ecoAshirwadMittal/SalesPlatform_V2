# Microflow Analysis: ACT_EmailAccount_ClientCredentialsGrant_SaveAutoConfig

### Requirements (Inputs):
- **$EmailProvider** (A record of type: Email_Connector.EmailProvider)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SelectedConfiguration****
2. **Create Object
      - Store the result in a new variable called **$NewIncomingEmailConfiguration****
3. **Create Object
      - Store the result in a new variable called **$NewOutgoingEmailConfiguration****
4. **Decision:** "IncomingSet?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **OutgoingSet?**
5. **Decision:** "Receive without selection?"
   - If [false] -> Move to: **Receive without selection?**
   - If [true] -> Move to: **Activity**
6. **Decision:** "Receive without selection?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Show Message**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
