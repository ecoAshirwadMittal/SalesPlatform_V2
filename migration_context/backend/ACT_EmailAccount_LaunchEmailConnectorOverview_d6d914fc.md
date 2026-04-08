# Microflow Analysis: ACT_EmailAccount_LaunchEmailConnectorOverview

### Execution Steps:
1. **Decision:** "EncryptionKey?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Search the Database for **Email_Connector.EmailTemplate** using filter: { Show everything } (Call this list **$FirstEmailTemplate**)**
3. **Decision:** "EmailTemplate?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
5. **Show Page**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
