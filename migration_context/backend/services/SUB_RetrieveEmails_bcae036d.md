# Microflow Analysis: SUB_RetrieveEmails

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$IncomingEmailConfiguration****
2. **Update the **$undefined** (Object):
      - Change [Email_Connector.IncomingEmailConfiguration_EmailAccount] to: "$IncomingEmailConfiguration"**
3. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName**** ⚠️ *(This step has a safety catch if it fails)*
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
