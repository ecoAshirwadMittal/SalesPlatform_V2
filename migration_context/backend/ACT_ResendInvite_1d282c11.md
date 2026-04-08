# Microflow Analysis: ACT_ResendInvite

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_CreateAndSendEmailForNewUser"
      - Store the result in a new variable called **$Variable****
2. **Show Message**
3. **Run another process: "EcoATM_UserManagement.SUB_SetUserOwnerAndChanger"**
4. **Permanently save **$undefined** to the database.**
5. **Create List
      - Store the result in a new variable called **$EcoATMDirectUserList****
6. **Change List**
7. **Run another process: "EcoATM_UserManagement.SUB_SendUserToSnowflake"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
