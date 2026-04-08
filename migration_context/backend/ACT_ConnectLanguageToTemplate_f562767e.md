# Microflow Analysis: ACT_ConnectLanguageToTemplate

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Decision:** "Does Email Template contains language ?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
2. **Retrieve
      - Store the result in a new variable called **$CurrentEmailTemplateLanguage****
3. **Retrieve
      - Store the result in a new variable called **$CurrentLanguage****
4. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [ForgotPassword.Configuration_EmailTemplate_Reset/ForgotPassword.Configuration
 and ForgotPassword.EmailTemplateLanguage_EmailTemplate/ForgotPassword.EmailTemplateLanguage/ForgotPassword.EmailTemplateLanguage_Language = $CurrentLanguage
 and id != $EmailTemplate]
 } (Call this list **$EmailTemplateListForLanguageComparison**)**
5. **Decision:** "Template found in selected language ?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Show Message**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
