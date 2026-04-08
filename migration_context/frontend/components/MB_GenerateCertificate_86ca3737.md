# Microflow Analysis: MB_GenerateCertificate

### Requirements (Inputs):
- **$Certificate_PrivateKey** (A record of type: Encryption.PGPCertificate)

### Execution Steps:
1. **Run another process: "Encryption.DS_GetAssociatedPubKey"
      - Store the result in a new variable called **$Certificate_PubKey****
2. **Java Action Call
      - Store the result in a new variable called **$Variable****
3. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$EncryptedPassword****
4. **Update the **$undefined** (Object):
      - Change [Encryption.PGPCertificate.PassPhrase_Plain] to: "empty"
      - Change [Encryption.PGPCertificate.PassPhrase_Encrypted] to: "$EncryptedPassword"
      - **Save:** This change will be saved to the database immediately.**
5. **Permanently save **$undefined** to the database.**
6. **Close Form**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
