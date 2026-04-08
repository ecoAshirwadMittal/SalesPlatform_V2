# Microflow Analysis: ACT_SendCohortMappingToSnowflake

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Search the Database for **EcoATM_Reports.CohortMapping** using filter: { Show everything } (Call this list **$CohortMappingList**)**
5. **Export Xml**
6. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName_1**** ⚠️ *(This step has a safety catch if it fails)*
7. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName**** ⚠️ *(This step has a safety catch if it fails)*
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
