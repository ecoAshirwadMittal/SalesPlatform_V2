# Microflow Analysis: SUB_SendPWSCounterOfferEmail

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='PWSCounterOffer']
 } (Call this list **$EmailTemplate**)**
4. **Decision:** "template found?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Run another process: "Custom_Logging.SUB_Log_Error"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
