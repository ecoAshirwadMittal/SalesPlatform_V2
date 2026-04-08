# Microflow Analysis: SUB_InvenNotificationProcessDateTime

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$CurrentDT** (A record of type: Object)

### Execution Steps:
1. **Decision:** "NotificationsEnabled?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Log Message**
3. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [
  (
    System.UserRoles = '[%UserRole_Bidder%]'
    and UserStatus != 'Disabled'
  )
] } (Call this list **$BidderList**)**
4. **Decision:** "IsStartNotificaitonSent?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **is Reminder Notification sent?**
5. **Log Message**
6. **Decision:** "Is current date/time = start date/time"
   - If [true] -> Move to: **Round 2?**
   - If [false] -> Move to: **Finish**
7. **Decision:** "Round 2?"
   - If [false] -> Move to: **Round 3?**
   - If [true] -> Move to: **Activity**
8. **Decision:** "Round 3?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
9. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/Status = 'Active']
[
  (
    BuyerCodeType = 'Data_Wipe' or BuyerCodeType = 'Wholesale'
  )
]
 } (Call this list **$BuyerCodeList_StartAuction**)**
10. **Create List
      - Store the result in a new variable called **$EcoATMDirectUserList_StartAuction****
11. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
12. **Take the list **$EcoATMDirectUserList_StartAuction**, perform a [Union], and call the result **$DistinctUsers_Start****
13. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
14. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction.isStartNotificationSent] to: "true"
      - **Save:** This change will be saved to the database immediately.**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
