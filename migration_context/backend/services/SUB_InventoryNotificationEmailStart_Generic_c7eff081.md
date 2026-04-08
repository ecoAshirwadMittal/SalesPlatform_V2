# Microflow Analysis: SUB_InventoryNotificationEmailStart_Generic

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "EcoATM_Direct_Sharepoint.ACT_GetSharepointBidRoundURL"
      - Store the result in a new variable called **$BidSheet_Url****
2. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
3. **Decision:** "Is user active?"
   - If [Active] -> Move to: **Activity**
   - If [Inactive] -> Move to: **Activity**
   - If [Disabled] -> Move to: **Finish**
   - If [(empty)] -> Move to: **Finish**
4. **Java Action Call
      - Store the result in a new variable called **$RootURL****
5. **Create Variable**
6. **Create Variable**
7. **Run another process: "AuctionUI.SUB_DateTimeEmailBody"
      - Store the result in a new variable called **$DateTime****
8. **Create Object
      - Store the result in a new variable called **$LinkObject****
9. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='Inventory_Notification_Start'] } (Call this list **$EmailTemplate**)**
10. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.To] to: "$EcoATMDirectUser/Name"**
11. **Java Action Call
      - Store the result in a new variable called **$Variable****
12. **Delete**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
