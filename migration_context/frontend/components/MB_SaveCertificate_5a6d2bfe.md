# Microflow Analysis: MB_SaveCertificate

### Requirements (Inputs):
- **$Certificate** (A record of type: Encryption.PGPCertificate)

### Execution Steps:
1. **Decision:** "cert type"
   - If [PrivateKey] -> Move to: **Pass phrase entered?**
   - If [PublicKey] -> Move to: **Finish**
   - If [(empty)] -> Move to: **Activity**
2. **Decision:** "Pass phrase entered?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
3. **Decision:** "Has pass phrase?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Java Action Call
      - Store the result in a new variable called **$Valid****
5. **Decision:** "Has Email?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
6. **Validation Feedback**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
