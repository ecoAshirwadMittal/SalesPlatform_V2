# Microflow Analysis: SUB_InventoryNotificationEmailStart_Round_3

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Run another process: "EcoATM_Direct_Sharepoint.ACT_GetSharepointBidRoundURL"
      - Store the result in a new variable called **$BidSheet_Url****
2. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
3. **Decision:** "Is user active?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Create Object
      - Store the result in a new variable called **$LinkObject****
5. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='Inventory_Notification_Start_Round3'] } (Call this list **$EmailTemplate**)**
6. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.To] to: "$EcoATMDirectUser/Name"**
7. **Java Action Call
      - Store the result in a new variable called **$Variable****
8. **Delete**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
