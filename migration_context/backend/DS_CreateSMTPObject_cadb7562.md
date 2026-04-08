# Microflow Analysis: DS_CreateSMTPObject

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$EmailAccount****
2. **Decision:** "Is template SMTP empty ?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Search the Database for **Email_Connector.EmailAccount** using filter: { [Username=$EmailTemplate/FromAddress and isOutgoingEmailConfigured] } (Call this list **$RetrieveAccountOfTemplate**)**
4. **Decision:** "is setSMTPAccountForEmailTemplate is not empty?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Create Object
      - Store the result in a new variable called **$NewTemplateSMTP****
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
