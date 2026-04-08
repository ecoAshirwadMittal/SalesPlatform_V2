# Microflow Analysis: SE_Cleanup

### Execution Steps:
1. **Log Message**
2. **Create Variable**
3. **Search the Database for **Email_Connector.EmailConnectorLog** using filter: { [Created<$TimePeriod] } (Call this list **$EmailLogList**)**
4. **Decision:** "Records found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Delete**

**Conclusion:** This process sends back a [Void] result.
