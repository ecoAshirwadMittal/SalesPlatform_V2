# Microflow Analysis: MB_OpenCertificateDetails

### Requirements (Inputs):
- **$Certificate** (A record of type: Encryption.PGPCertificate)

### Execution Steps:
1. **Decision:** "cert type"
   - If [(empty)] -> Move to: **Activity**
   - If [PublicKey] -> Move to: **Activity**
   - If [PrivateKey] -> Move to: **Activity**
2. **Show Message**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
