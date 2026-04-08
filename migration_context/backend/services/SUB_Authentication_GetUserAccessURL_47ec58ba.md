# Microflow Analysis: SUB_Authentication_GetUserAccessURL

### Requirements (Inputs):
- **$Authentication** (A record of type: MicrosoftGraph.Authentication)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Authorization_GetOrCreate"
      - Store the result in a new variable called **$Authorization****
2. **Java Action Call
      - Store the result in a new variable called **$URL****
3. **Run another process: "MicrosoftGraph.SUB_Scopes_Get"
      - Store the result in a new variable called **$Scope****
4. **Run another process: "MicrosoftGraph.SUB_Authorization_SetNonce"
      - Store the result in a new variable called **$Nonce****
5. **Create Variable**
6. **Permanently save **$undefined** to the database.** ⚠️ *(This step has a safety catch if it fails)*
7. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
