# Microflow Analysis: BCO_Pk12Certificate_EncryptPassphrase

### Requirements (Inputs):
- **$Pk12Certificate** (A record of type: Email_Connector.Pk12Certificate)

### Execution Steps:
1. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$encryptedPWD**** ⚠️ *(This step has a safety catch if it fails)*
2. **Update the **$undefined** (Object):
      - Change [Email_Connector.Pk12Certificate.Passphrase] to: "$encryptedPWD"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
