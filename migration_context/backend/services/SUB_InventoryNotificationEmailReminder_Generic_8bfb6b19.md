# Microflow Analysis: SUB_InventoryNotificationEmailReminder_Generic

### Requirements (Inputs):
- **$ReminderNotificationHelper** (A record of type: AuctionUI.ReminderNotificationHelper)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
2. **Decision:** "Is user active?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Run another process: "AuctionUI.ACT_CreateActivateUserURL"
      - Store the result in a new variable called **$URL_Inactive****
4. **Run another process: "AuctionUI.SUB_DateTimeEmailBody"
      - Store the result in a new variable called **$DateTime_Inactive****
5. **Create Object
      - Store the result in a new variable called **$LinkObject_Inactive****
6. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='Inventory_Notification_Reminder'] } (Call this list **$EmailTemplate_Inactive**)**
7. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.To] to: "$ReminderNotificationHelper/Email"**
8. **Java Action Call
      - Store the result in a new variable called **$Variable****
9. **Delete**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
