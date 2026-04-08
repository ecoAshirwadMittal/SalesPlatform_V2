# Microflow Analysis: SUB_SendCaseLotsToSnowflake

### Requirements (Inputs):
- **$JSON_CaseLotData** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Create Variable**
5. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
6. **Create Object
      - Store the result in a new variable called **$ARG_SON_CONTENT****
7. **Change List**
8. **Create Variable**
9. **Java Action Call
      - Store the result in a new variable called **$IsSuccess**** ⚠️ *(This step has a safety catch if it fails)*
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"
      - Store the result in a new variable called **$Log****
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
