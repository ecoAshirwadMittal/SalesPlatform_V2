# Microflow Analysis: BCO_OAuthToken_EncryptTokens

### Requirements (Inputs):
- **$OAuthToken** (A record of type: Email_Connector.OAuthToken)

### Execution Steps:
1. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$access_token****
2. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$refresh_token****
3. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$id_token****
4. **Update the **$undefined** (Object):
      - Change [Email_Connector.OAuthToken.access_token] to: "$access_token"
      - Change [Email_Connector.OAuthToken.refresh_token] to: "$refresh_token"
      - Change [Email_Connector.OAuthToken.id_token] to: "$id_token"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
