# Microflow Analysis: SUB_InventoryNotificationEmailReminder_Round_2

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)
- **$ReminderNotificationHelper** (A record of type: AuctionUI.ReminderNotificationHelper)
- **$BuyerCodeList_Round2Selected** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerList_Round2****
2. **Create Variable**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
5. **Decision:** "Is user active?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Run another process: "AuctionUI.ACT_CreateActivateUserURL"
      - Store the result in a new variable called **$URL_Inactive****
7. **Run another process: "AuctionUI.SUB_DateTimeEmailBody"
      - Store the result in a new variable called **$DateTime_Inactive****
8. **Create Object
      - Store the result in a new variable called **$LinkObject_Inactive****
9. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='Inventory_Notification_Reminder_Round2'] } (Call this list **$EmailTemplate_Inactive**)**
10. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.To] to: "$ReminderNotificationHelper/Email"**
11. **Java Action Call
      - Store the result in a new variable called **$Variable****
12. **Delete**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
