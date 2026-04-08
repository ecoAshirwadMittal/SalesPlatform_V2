# Microflow Analysis: SE_LoadDAData

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Run another process: "EcoATM_DA.SUB_GetOrCreateDAWeekCurrentWeek"
      - Store the result in a new variable called **$DAWeek****
3. **Run another process: "EcoATM_DA.SUB_LoadDAData"
      - Store the result in a new variable called **$Variable**** ⚠️ *(This step has a safety catch if it fails)*
4. **Run another process: "Custom_Logging.SUB_Log_Info"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
