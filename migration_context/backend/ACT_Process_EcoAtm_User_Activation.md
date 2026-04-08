# Microflow Detailed Specification: ACT_Process_EcoAtm_User_Activation

### 📥 Inputs (Parameters)
- **$ForgotPassword** (Type: ForgotPassword.ForgotPassword)

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_UserManagement.EcoATMDirectUser** Filter: `[(Email = $ForgotPassword/EmailAddress)]` (Result: **$EcoATMDirectUser**)**
2. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
3. **Update **$EcoATMDirectUser** (and Save to DB)
      - Set **ActivationDate** = `[%CurrentDateTime%]`
      - Set **UserStatus** = `AuctionUI.enum_UserStatus.Active`**
4. **Delete**
5. **CreateList**
6. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
7. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
8. **Maps to Page: **AuctionUI.Login_Custom_Web****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.