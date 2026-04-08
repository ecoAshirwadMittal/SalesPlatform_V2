# Microflow Analysis: SUB_EmailAccount_OAuthToken_ReplaceExpired

### Requirements (Inputs):
- **$ExpiredOAuthToken** (A record of type: Email_Connector.OAuthToken)
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)
- **$NewOAuthToken** (A record of type: Email_Connector.OAuthToken)

### Execution Steps:
1. **Delete**
2. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailAccount_OAuthToken] to: "$EmailAccount"
      - **Save:** This change will be saved to the database immediately.**
3. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailAccount_OAuthToken] to: "$NewOAuthToken"
      - **Save:** This change will be saved to the database immediately.**
4. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
