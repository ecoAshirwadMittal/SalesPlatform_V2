# Microflow Detailed Specification: ACT_EcoATMDIrectUser_UpdateByAdmin

### 📥 Inputs (Parameters)
- **$EcoATMDirectUser** (Type: EcoATM_UserManagement.EcoATMDirectUser)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Call Microflow **Custom_Logging.SUB_Log_Warning****
3. **Commit/Save **$EcoATMDirectUser** to Database**
4. **Call Microflow **EcoATM_UserManagement.SUB_SetUserOwnerAndChanger****
5. **CreateList**
6. **Add **$$EcoATMDirectUser** to/from list **$EcoATMDirectUserList****
7. **Call Microflow **EcoATM_UserManagement.SUB_SendUserToSnowflake****
8. **DB Retrieve **System.UserRole** Filter: `[ ( Name = 'Bidder' ) ]` (Result: **$BidderRole**)**
9. 🔀 **DECISION:** `$BidderRole!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_UserManagement.SUB_SendBuyerUserToSnowflake****
      2. **Close current page/popup**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Close current page/popup**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.