# Microflow Analysis: SUB_SendAllSalesRepresentativeToSnowflake

### Requirements (Inputs):
- **$SalesRepresentativeList** (A record of type: EcoATM_BuyerManagement.SalesRepresentative)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
3. **Create Variable**
4. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
5. **Create Object
      - Store the result in a new variable called **$ArgumentJSON_CONTENT****
6. **Change List**
7. **Create Variable**
8. **Run another process: "Custom_Logging.SUB_Log_Info"**
9. **Java Action Call
      - Store the result in a new variable called **$IsSuccess**** ⚠️ *(This step has a safety catch if it fails)*
10. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
