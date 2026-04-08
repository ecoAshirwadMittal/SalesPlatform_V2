# Microflow Analysis: BCO_OAuthProvider_EncryptPassword

### Requirements (Inputs):
- **$OAuthProvider** (A record of type: Email_Connector.OAuthProvider)

### Execution Steps:
1. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$EncClientSecret****
2. **Update the **$undefined** (Object):
      - Change [Email_Connector.OAuthProvider.ClientSecret] to: "$EncClientSecret"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
