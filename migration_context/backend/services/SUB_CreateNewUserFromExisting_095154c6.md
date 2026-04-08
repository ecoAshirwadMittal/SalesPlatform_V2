# Microflow Analysis: SUB_CreateNewUserFromExisting

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Create Object
      - Store the result in a new variable called **$NewEcoATMDirectUser****
5. **Update the **$undefined** (Object):
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
      - Change [EcoATM_UserManagement.EcoATMDirectUser_Buyer] to: "$EcoATMDirectUser/EcoATM_UserManagement.EcoATMDirectUser_Buyer"**
6. **Run another process: "AuctionUI.ACT_SaveNewUser"**
7. **Delete**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
