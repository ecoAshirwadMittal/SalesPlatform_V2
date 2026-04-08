# Microflow Analysis: ACT_EmailMessageList_ResetQueuedStatus

### Requirements (Inputs):
- **$EmailList** (A record of type: Email_Connector.EmailMessage)

### Execution Steps:
1. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
2. **Permanently save **$undefined** to the database.**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
