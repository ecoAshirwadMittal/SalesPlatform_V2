# Microflow Analysis: SUB_GetNewOAuthToken_ClientCredentialsGrantFlow

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)
- **$OAuthProvider** (A record of type: Email_Connector.OAuthProvider)

### Execution Steps:
1. **Run another process: "Email_Connector.SUB_Decrypt"
      - Store the result in a new variable called **$clientSecret****
2. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
3. **Decision:** "Success?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
