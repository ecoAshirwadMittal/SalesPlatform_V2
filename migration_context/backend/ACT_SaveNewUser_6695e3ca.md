# Microflow Analysis: ACT_SaveNewUser

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Run another process: "AuctionUI.VAL_EcoAtmUser"
      - Store the result in a new variable called **$IsValid****
2. **Decision:** "Valid?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Search the Database for **System.Language** using filter: { [Code = 'en_US'] } (Call this list **$Language**)**
4. **Run another process: "EcoATM_UserManagement.SUB_SetUserOwnerAndChanger"**
5. **Run another process: "AuctionUI.ACT_CreateAndSendEmailForNewUser"
      - Store the result in a new variable called **$Variable****
6. **Run another process: "AuctionUI.ACT_SaveUserRolesAndBuyerDisplay"**
7. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.EcoATMDirectUser.FirstName] to: "$EcoATMDirectUser/FirstName"
      - Change [EcoATM_UserManagement.EcoATMDirectUser.LastName] to: "$EcoATMDirectUser/LastName"
      - Change [Administration.Account.FullName] to: "$EcoATMDirectUser/FirstName + ' ' + $EcoATMDirectUser/LastName"
      - Change [Administration.Account.Email] to: "toLowerCase($EcoATMDirectUser/Email)"
      - Change [System.User.Password] to: "'EcoAtm@022024'"
      - Change [Administration.Account.IsLocalUser] to: "if contains($EcoATMDirectUser/Email, 'ecoatm.com') then false else true"
      - Change [System.User.Name] to: "$EcoATMDirectUser/Email"
      - Change [System.UserRoles] to: "$EcoATMDirectUser/System.UserRoles"
      - Change [EcoATM_UserManagement.EcoATMDirectUser_Buyer] to: "$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer"
      - Change [EcoATM_UserManagement.EcoATMDirectUser.Inactive] to: "true"
      - Change [System.User_Language] to: "$Language"
      - **Save:** This change will be saved to the database immediately.**
8. **Log Message**
9. **Create List
      - Store the result in a new variable called **$EcoATMDirectUserList****
10. **Change List**
11. **Run another process: "EcoATM_UserManagement.SUB_SendUserToSnowflake"**
12. **Decision:** "is Bidder?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
13. **Run another process: "EcoATM_UserManagement.SUB_SendBuyerUserToSnowflake"**
14. **Close Form**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
