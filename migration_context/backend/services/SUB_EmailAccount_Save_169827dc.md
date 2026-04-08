# Microflow Analysis: SUB_EmailAccount_Save

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Decision:** "IncomingEmailConfiguration?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
2. **Retrieve
      - Store the result in a new variable called **$IncomingEmailConfiguration_2****
3. **Delete**
4. **Decision:** "OutgoingEmailConfiguration ?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Retrieve
      - Store the result in a new variable called **$OutgoingEmailConfiguration****
6. **Permanently save **$undefined** to the database.**
7. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailAccount.isOutgoingEmailConfigured] to: "true"**
8. **Permanently save **$undefined** to the database.**
9. **Close Form**
10. **Show Page**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
