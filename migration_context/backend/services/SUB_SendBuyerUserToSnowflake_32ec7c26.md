# Microflow Analysis: SUB_SendBuyerUserToSnowflake

### Requirements (Inputs):
- **$EcoATMDirectUserList** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
5. **Create Variable**
6. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
7. **Create Object
      - Store the result in a new variable called **$ArgumentJSON_CONTENT****
8. **Change List**
9. **Create Variable**
10. **Java Action Call
      - Store the result in a new variable called **$IsSuccess**** ⚠️ *(This step has a safety catch if it fails)*
11. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
