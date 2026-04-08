# Microflow Analysis: Sub_ResumableUpload

### Requirements (Inputs):
- **$UploadURL** (A record of type: Object)
- **$FileDocument** (A record of type: System.FileDocument)
- **$Authorization** (A record of type: MicrosoftGraph.Authorization)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Encryption.Decrypt"
      - Store the result in a new variable called **$DecryptedToken****
3. **Java Action Call
      - Store the result in a new variable called **$FileFragment****
4. **Rest Call**
5. **Delete**
6. **Change Variable**
7. **Decision:** "End reached?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
