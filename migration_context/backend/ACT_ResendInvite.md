# Microflow Detailed Specification: ACT_ResendInvite

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.ACT_CreateAndSendEmailForNewUser** (Result: **$Variable**)**
2. **Show Message (Information): `Message has been resent to {1}.`**
3. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
4. **Commit/Save **$EcoATMDirectUser** to Database**
5. **CreateList**
6. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
7. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.