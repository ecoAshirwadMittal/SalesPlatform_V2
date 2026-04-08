# Microflow Analysis: ACT_SendCounterOfferReminderEmails

### Requirements (Inputs):
- **$Offer** (A record of type: EcoATM_PWS.Offer)

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.PWSConstants** using filter: { Show everything } (Call this list **$PWSConstants**)**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_Info"**
4. **Decision:** "send 2nd reminder email?"
   - If [true] -> Move to: **2nd reminder hours 
passed?**
   - If [false] -> Move to: **Finish**
5. **Decision:** "2nd reminder hours 
passed?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Run another process: "EcoATM_PWS.SUB_SendSecondReminderEmail"**
7. **Update the **$undefined** (Object):
      - Change [EcoATM_PWS.Offer.SecondReminderSent] to: "true"
      - **Save:** This change will be saved to the database immediately.**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
