# Microflow Analysis: SUB_SendRMADetailsToSnowflake

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Run another process: "EcoATM_RMA.SUB_SetRMAOwnerAndChanger"**
5. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
6. **Create Variable**
7. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
8. **Create Object
      - Store the result in a new variable called **$ArgumentJSON_CONTENT****
9. **Change List**
10. **Create Variable**
11. **Java Action Call
      - Store the result in a new variable called **$IsSuccess**** ⚠️ *(This step has a safety catch if it fails)*
12. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
