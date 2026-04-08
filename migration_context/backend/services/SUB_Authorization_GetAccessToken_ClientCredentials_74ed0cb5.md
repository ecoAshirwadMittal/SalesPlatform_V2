# Microflow Analysis: SUB_Authorization_GetAccessToken_ClientCredentials

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Run another process: "Encryption.Decrypt"
      - Store the result in a new variable called **$Secret****
2. **Java Action Call
      - Store the result in a new variable called **$URL****
3. **Create Variable**
4. **Create Variable**
5. **Run another process: "MicrosoftGraph.POST"
      - Store the result in a new variable called **$Response****
6. **Decision:** "Successful?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authorization.AdminConsent] to: "true"
      - Change [MicrosoftGraph.Authorization.GrantFlow] to: "MicrosoftGraph.ENUM_GrantFlow.client_credentials"**
8. **Run another process: "MicrosoftGraph.SUB_Authorization_ProcessSuccessfulResponse"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
