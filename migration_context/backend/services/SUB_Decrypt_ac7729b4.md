# Microflow Analysis: SUB_Decrypt

### Requirements (Inputs):
- **$EncryptedString** (A record of type: Object)
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Run another process: "Encryption.Decrypt"
      - Store the result in a new variable called **$decryptedString**** ⚠️ *(This step has a safety catch if it fails)*
2. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
