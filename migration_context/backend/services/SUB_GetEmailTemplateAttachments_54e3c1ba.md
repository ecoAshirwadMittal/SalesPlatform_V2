# Microflow Analysis: SUB_GetEmailTemplateAttachments

### Requirements (Inputs):
- **$FileDocumentList** (A record of type: System.FileDocument)
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Create List
      - Store the result in a new variable called **$AttachmentList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
