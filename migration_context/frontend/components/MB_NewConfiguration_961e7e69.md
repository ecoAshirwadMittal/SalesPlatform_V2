# Microflow Analysis: MB_NewConfiguration

### Execution Steps:
1. **Run another process: "SAML20.DS_GetSPMetadata"
      - Store the result in a new variable called **$SPMetadata****
2. **Run another process: "SAML20.SPMetadata_Validate"
      - Store the result in a new variable called **$Valid****
3. **Decision:** "Valid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Show Message**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
