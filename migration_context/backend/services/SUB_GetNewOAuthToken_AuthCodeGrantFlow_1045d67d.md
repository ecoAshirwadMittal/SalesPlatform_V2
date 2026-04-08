# Microflow Analysis: SUB_GetNewOAuthToken_AuthCodeGrantFlow

### Requirements (Inputs):
- **$OAuthProvider** (A record of type: Email_Connector.OAuthProvider)
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)
- **$OAuthToken** (A record of type: Email_Connector.OAuthToken)

### Execution Steps:
1. **Run another process: "Email_Connector.SUB_Decrypt"
      - Store the result in a new variable called **$clientSecret****
2. **Run another process: "Email_Connector.SUB_Decrypt"
      - Store the result in a new variable called **$refreshToken****
3. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
4. **Decision:** "Success?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
