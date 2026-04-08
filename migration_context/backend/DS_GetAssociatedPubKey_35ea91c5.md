# Microflow Analysis: DS_GetAssociatedPubKey

### Requirements (Inputs):
- **$Certificate** (A record of type: Encryption.PGPCertificate)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$PublicKey****
2. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
