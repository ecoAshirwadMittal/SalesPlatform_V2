# Microflow Analysis: ACT_EmailTemplate_DuplicateAndShowPage

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$TokenList****
2. **Run another process: "Email_Connector.SUB_DuplicateTokenList"
      - Store the result in a new variable called **$NewTokenList****
3. **Retrieve
      - Store the result in a new variable called **$AttachmentList****
4. **Create Object
      - Store the result in a new variable called **$NewEmailTemplate****
5. **Run another process: "Email_Connector.SUB_GetEmailTemplateAttachments"
      - Store the result in a new variable called **$AttachmentList_New****
6. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.TemplateName] to: "$EmailTemplate/TemplateName"
      - Change [Email_Connector.EmailTemplate.hasAttachment] to: "$AttachmentList_New!=empty"**
7. **Show Page**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
