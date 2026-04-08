# Microflow Analysis: SUB_SendCounterOfferReminderEmail

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.PWSConstants** using filter: { Show everything } (Call this list **$PWSConstants**)**
2. **Search the Database for **EcoATM_PWS.Offer** using filter: { [OfferStatus = 'Buyer_Acceptance']
[FirstReminderSent=false or SecondReminderSent=false] } (Call this list **$OfferList**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
