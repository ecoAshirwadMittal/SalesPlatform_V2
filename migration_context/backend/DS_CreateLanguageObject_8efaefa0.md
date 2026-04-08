# Microflow Analysis: DS_CreateLanguageObject

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$TemplateLanguage****
2. **Decision:** "Is template language empty ?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$NewTemplateLanguage****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
