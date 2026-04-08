# Microflow Analysis: SUB_SendUserLoginToSnowflake

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Create Variable**
5. **Create Variable**
6. **Create Variable**
7. **Create Variable**
8. **Java Action Call
      - Store the result in a new variable called **$isSuccess**** ⚠️ *(This step has a safety catch if it fails)*
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
