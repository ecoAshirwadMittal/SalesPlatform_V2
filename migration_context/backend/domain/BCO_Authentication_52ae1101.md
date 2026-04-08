# Microflow Analysis: BCO_Authentication

### Requirements (Inputs):
- **$Authentication** (A record of type: MicrosoftGraph.Authentication)

### Execution Steps:
1. **Decision:** "Check if "$Authentication/ConsumerSecret" is encrypted"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
2. **Run another process: "Encryption.Encrypt"
      - Store the result in a new variable called **$EncryptedSecret****
3. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authentication.Client_Secret] to: "$EncryptedSecret"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
