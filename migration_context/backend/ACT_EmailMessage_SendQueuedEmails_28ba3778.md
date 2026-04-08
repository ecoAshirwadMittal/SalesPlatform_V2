# Microflow Analysis: ACT_EmailMessage_SendQueuedEmails

### Requirements (Inputs):
- **$EmailAccount** (A record of type: Email_Connector.EmailAccount)

### Execution Steps:
1. **Log Message**
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Search the Database for **Email_Connector.EmailMessage** using filter: { [Email_Connector.EmailMessage_EmailAccount = $EmailAccount and QueuedForSending = true() and (Status = 'QUEUED' or Status = 'ERROR')] } (Call this list **$EmailList**)**
4. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
5. **Aggregate List
      - Store the result in a new variable called **$EmailsProcessed****
6. **Decision:** "More than 1 handled?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
7. **Log Message**
8. **Permanently save **$undefined** to the database.**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
