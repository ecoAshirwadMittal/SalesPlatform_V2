# Microflow Analysis: SE_LogCleanUp

### Execution Steps:
1. **Search the Database for **SAML20.SPMetadata** using filter: { Show everything } (Call this list **$Configuration**)**
2. **Create Variable**
3. **Decision:** "empty"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
4. **Change Variable**
5. **Search the Database for **SAML20.SAMLRequest** using filter: { [createdDate < $DeleteBeforeDate] } (Call this list **$SAMLRequestList**)**
6. **Decision:** "Found?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
7. **Search the Database for **SAML20.SSOLog** using filter: { [createdDate < $DeleteBeforeDate] } (Call this list **$SSOLogList**)**
8. **Decision:** "Found?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
