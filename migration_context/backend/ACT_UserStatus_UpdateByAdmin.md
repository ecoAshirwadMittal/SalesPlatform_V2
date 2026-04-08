# Microflow Detailed Specification: ACT_UserStatus_UpdateByAdmin

### 📥 Inputs (Parameters)
- **$UserStatus** (Type: EcoATM_UserManagement.UserStatus)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. **Call Microflow **Custom_Logging.SUB_Log_Warning****
3. **Commit/Save **$UserStatus** to Database**
4. **Close current page/popup**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.