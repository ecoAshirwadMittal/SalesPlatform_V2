# Microflow Analysis: ACT_Round3_StartNotification

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)

### Execution Steps:
1. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [
  (
    System.UserRoles = '[%UserRole_SalesRep%]' or System.UserRoles = '[%UserRole_SalesOps%]'
  )
] } (Call this list **$SalesRepOpsList**)**
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction.isStartNotificationSent] to: "true"
      - **Save:** This change will be saved to the database immediately.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
