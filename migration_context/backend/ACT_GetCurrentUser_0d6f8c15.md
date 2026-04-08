# Microflow Analysis: ACT_GetCurrentUser

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [(Name = $currentUser/Name)] } (Call this list **$EcoATMDirectUser**)** ⚠️ *(This step has a safety catch if it fails)*
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
