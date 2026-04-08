# Microflow Analysis: DV_GetConfiguration

### Execution Steps:
1. **Search the Database for **ForgotPassword.Configuration** using filter: { Show everything } (Call this list **$Configuration**)**
2. **Decision:** "found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Search the Database for **Email_Connector.EmailTemplate** using filter: { Show everything } (Call this list **$EmailTemplateList**)**
4. **Decision:** "Email connector has templates?"
   - If [true] -> Move to: **Configuration_EmailTemplate_Reset Association not exists?**
   - If [false] -> Move to: **Finish**
5. **Decision:** "Configuration_EmailTemplate_Reset Association not exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [contains(TemplateName,'_Reset')] } (Call this list **$EmailTemplateReset**)**
7. **Decision:** "Email template for reset not found?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
8. **Update the **$undefined** (Object):
      - Change [ForgotPassword.Configuration_EmailTemplate_Reset] to: "$EmailTemplateReset"**
9. **Decision:** "Configuration_EmailTemplate_Signup Association not exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
10. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [contains(TemplateName,'_Signup')] } (Call this list **$EmailTemplateSignup**)**
11. **Decision:** "Email template for signup not exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
12. **Update the **$undefined** (Object):
      - Change [ForgotPassword.Configuration_EmailTemplate_Signup] to: "$EmailTemplateSignup"
      - **Save:** This change will be saved to the database immediately.**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
