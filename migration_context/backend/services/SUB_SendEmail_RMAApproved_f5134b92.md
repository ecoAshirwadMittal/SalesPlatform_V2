# Microflow Analysis: SUB_SendEmail_RMAApproved

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='PWSRMAApprovalEmail']
 } (Call this list **$EmailTemplate**)**
3. **Decision:** "template found?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Run another process: "Custom_Logging.SUB_Log_Error"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
