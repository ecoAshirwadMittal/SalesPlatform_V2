# Microflow Analysis: ACT_Process_EcoAtm_User_Activation

### Requirements (Inputs):
- **$ForgotPassword** (A record of type: ForgotPassword.ForgotPassword)

### Execution Steps:
1. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [(Email = $ForgotPassword/EmailAddress)] } (Call this list **$EcoATMDirectUser**)**
2. **Run another process: "EcoATM_UserManagement.SUB_SetUserOwnerAndChanger"**
3. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.EcoATMDirectUser.ActivationDate] to: "[%CurrentDateTime%]"
      - Change [EcoATM_UserManagement.EcoATMDirectUser.UserStatus] to: "AuctionUI.enum_UserStatus.Active"
      - **Save:** This change will be saved to the database immediately.**
4. **Delete**
5. **Create List
      - Store the result in a new variable called **$EcoATMDirectUserList****
6. **Change List**
7. **Run another process: "EcoATM_UserManagement.SUB_SendUserToSnowflake"**
8. **Show Page**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
