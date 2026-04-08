# Microflow Analysis: DS_GetSPMetadata

### Execution Steps:
1. **Search the Database for **SAML20.SPMetadata** using filter: { Show everything } (Call this list **$SPMetadata**)**
2. **Decision:** "found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
