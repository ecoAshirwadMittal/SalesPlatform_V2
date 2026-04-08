# Microflow Analysis: SUB_SendPWSOfferConfirmationEmail

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
2. **Take the list **$UserRoleList**, perform a [Find] where: { 'Bidder' }, and call the result **$Bidder****
3. **Decision:** "Bidder?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
5. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
6. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='PWSOfferConfirmation']
 } (Call this list **$EmailTemplate**)**
7. **Decision:** "template found?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
8. **Run another process: "Custom_Logging.SUB_Log_Error"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
