# Microflow Analysis: SUB_InventoryNotificationEmailStart_Round_2

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$BuyerCodeList_Round2Selected** (A record of type: EcoATM_BuyerManagement.BuyerCode)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerList_Round2****
2. **Create Variable**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
5. **Decision:** "Is user active?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Java Action Call
      - Store the result in a new variable called **$RootURL****
7. **Create Variable**
8. **Create Variable**
9. **Run another process: "AuctionUI.SUB_DateTimeEmailBody"
      - Store the result in a new variable called **$DateTime****
10. **Create Object
      - Store the result in a new variable called **$LinkObject****
11. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='Inventory_Notification_Start_Round2'] } (Call this list **$EmailTemplate**)**
12. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.To] to: "$EcoATMDirectUser/Name"**
13. **Java Action Call
      - Store the result in a new variable called **$Variable****
14. **Delete**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
