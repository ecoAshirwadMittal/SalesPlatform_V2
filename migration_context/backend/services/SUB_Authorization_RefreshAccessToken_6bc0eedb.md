# Microflow Analysis: SUB_Authorization_RefreshAccessToken

### Requirements (Inputs):
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Decision:** "Check if "$Authorization/Refresh_Token" exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Log Message**
3. **Run another process: "Encryption.Decrypt"
      - Store the result in a new variable called **$DecryptedRefreshToken****
4. **Run another process: "Encryption.Decrypt"
      - Store the result in a new variable called **$DecryptedSecret****
5. **Java Action Call
      - Store the result in a new variable called **$URL****
6. **Run another process: "MicrosoftGraph.SUB_Scopes_Get"
      - Store the result in a new variable called **$Scope****
7. **Create Variable**
8. **Create Variable**
9. **Run another process: "MicrosoftGraph.POST"
      - Store the result in a new variable called **$Response****
10. **Decision:** "Successful?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Sub microflow**
11. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authorization.Successful] to: "false"
      - **Save:** This change will be saved to the database immediately.**
12. **Import Xml**
13. **Log Message**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
