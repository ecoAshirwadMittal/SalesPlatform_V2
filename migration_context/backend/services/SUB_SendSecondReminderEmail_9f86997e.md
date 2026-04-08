# Microflow Analysis: SUB_SendSecondReminderEmail

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='PWSCounterOfferSecondReminderEmail']
 } (Call this list **$EmailTemplate**)**
3. **Decision:** "template found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
5. **Retrieve
      - Store the result in a new variable called **$BuyerCode****
6. **Retrieve
      - Store the result in a new variable called **$OfferItemList****
7. **Create Variable**
8. **Create Variable**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Change Variable**
11. **Create Variable**
12. **Create Variable**
13. **Create Variable**
14. **Retrieve
      - Store the result in a new variable called **$Buyer****
15. **Retrieve
      - Store the result in a new variable called **$EcoATMDirectUserList****
16. **Create List
      - Store the result in a new variable called **$PWSEmailNotificationList****
17. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
18. **Delete**
19. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
20. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
