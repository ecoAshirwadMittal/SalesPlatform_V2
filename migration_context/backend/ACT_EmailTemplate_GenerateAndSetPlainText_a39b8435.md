# Microflow Analysis: ACT_EmailTemplate_GenerateAndSetPlainText

### Requirements (Inputs):
- **$EmailTemplate** (A record of type: Email_Connector.EmailTemplate)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$PlainText****
2. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.PlainBody] to: "$PlainText"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
