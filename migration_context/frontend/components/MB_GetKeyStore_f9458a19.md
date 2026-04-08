# Microflow Analysis: MB_GetKeyStore

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$KeyStore****
2. **Decision:** "'is Key-store avaliable'"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "Encryption.Decrypt"
      - Store the result in a new variable called **$DecrptPassword****
4. **Update the **$undefined** (Object):
      - Change [SAML20.KeyStore.Password] to: "$DecrptPassword"
      - **Save:** This change will be saved to the database immediately.**
5. **Show Page**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
