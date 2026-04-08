# Microflow Analysis: ACT_OAuthProvider_ShowOAuthProviderPage

### Requirements (Inputs):
- **$OAuthProvider** (A record of type: Email_Connector.OAuthProvider)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$EmailAccountList****
2. **Take the list **$EmailAccountList**, perform a [Head], and call the result **$EmailAccount****
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
