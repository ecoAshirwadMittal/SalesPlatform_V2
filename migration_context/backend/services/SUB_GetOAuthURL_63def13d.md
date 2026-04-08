# Microflow Analysis: SUB_GetOAuthURL

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$OAuthProvider****
2. **Retrieve
      - Store the result in a new variable called **$ScopeSelectedList****
3. **Create Variable**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Create Variable**
6. **Run another process: "Email_Connector.SUB_GenerateOAuthNonce"
      - Store the result in a new variable called **$nonce****
7. **Create Object
      - Store the result in a new variable called **$NewOAuthNonce****
8. **Create Variable**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
