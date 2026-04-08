# Microflow Analysis: BCO_EmailAccount_EncryptPassword

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$encryptedPassword**** ⚠️ *(This step has a safety catch if it fails)*
2. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailAccount.Password] to: "$encryptedPassword"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
