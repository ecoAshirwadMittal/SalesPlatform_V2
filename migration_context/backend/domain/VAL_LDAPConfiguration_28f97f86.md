# Microflow Analysis: VAL_LDAPConfiguration

### Requirements (Inputs):
- **$EmailMessage** (A record of type: Email_Connector.EmailMessage)

### Execution Steps:
1. **Decision:** "isEncrypted?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Retrieve
      - Store the result in a new variable called **$EmailAccount****
3. **Decision:** "isLDAPConfigured?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Show Message**
5. **Show Page**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
