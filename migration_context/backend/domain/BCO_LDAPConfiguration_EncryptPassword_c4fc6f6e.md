# Microflow Analysis: BCO_LDAPConfiguration_EncryptPassword

### Requirements (Inputs):
- **$LDAPConfiguration** (A record of type: Email_Connector.LDAPConfiguration)

### Execution Steps:
1. **Decision:** "LDAPAuthType.simple?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$encryptedPWD**** ⚠️ *(This step has a safety catch if it fails)*
3. **Update the **$undefined** (Object):
      - Change [Email_Connector.LDAPConfiguration.LDAPPassword] to: "$encryptedPWD"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
