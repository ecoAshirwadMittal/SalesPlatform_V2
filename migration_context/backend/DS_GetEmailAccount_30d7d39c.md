# Microflow Analysis: DS_GetEmailAccount

### Execution Steps:
1. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
2. **Decision:** "is empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Retrieve
      - Store the result in a new variable called **$IncomingEmailConfiguration****
4. **Decision:** "is empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Create Object
      - Store the result in a new variable called **$NewIncomingEmailConfiguration****
6. **Update the **$undefined** (Object):
      - Change [Email_Connector.IncomingEmailConfiguration_EmailAccount] to: "$NewIncomingEmailConfiguration"**
7. **Retrieve
      - Store the result in a new variable called **$OutgoingEmailConfiguration****
8. **Decision:** "is empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
9. **Create Object
      - Store the result in a new variable called **$NewOutgoingEmailConfiguration****
10. **Update the **$undefined** (Object):
      - Change [Email_Connector.OutgoingEmailConfiguration_EmailAccount] to: "$NewOutgoingEmailConfiguration"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
