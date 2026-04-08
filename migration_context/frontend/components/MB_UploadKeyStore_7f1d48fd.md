# Microflow Analysis: MB_UploadKeyStore

### Requirements (Inputs):
- **$KeyStore** (A record of type: SAML20.KeyStore)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SSOConfiguration****
2. **Create Variable**
3. **Create Variable**
4. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$EncryptedPassword****
5. **Update the **$undefined** (Object):
      - Change [SAML20.KeyStore.Password] to: "$EncryptedPassword"
      - **Save:** This change will be saved to the database immediately.**
6. **Java Action Call
      - Store the result in a new variable called **$isValidAliasName****
7. **Decision:** "'is valid alias name'"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
8. **Update the **$undefined** (Object):
      - Change [SAML20.KeyStore.LastChangedOn] to: "[%CurrentDateTime%]"
      - **Save:** This change will be saved to the database immediately.**
9. **Close Form**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
