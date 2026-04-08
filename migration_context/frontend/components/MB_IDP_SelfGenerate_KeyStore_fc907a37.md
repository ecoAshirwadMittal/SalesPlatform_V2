# Microflow Analysis: MB_IDP_SelfGenerate_KeyStore

### Requirements (Inputs):
- **$SSOConfiguration** (A record of type: SAML20.SSOConfiguration)

### Execution Steps:
1. **Decision:** "is Encryption enabled?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Retrieve
      - Store the result in a new variable called **$KeyStore****
3. **Decision:** "is key-store empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Search the Database for **SAML20.SSOConfiguration** using filter: { [id = $SSOConfiguration] } (Call this list **$ReloadSSOConfiguration**)**
5. **Java Action Call
      - Store the result in a new variable called **$SelfGeneKeyStroe****
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
