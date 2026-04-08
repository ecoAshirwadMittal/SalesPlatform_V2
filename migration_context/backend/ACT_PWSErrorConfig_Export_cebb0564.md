# Microflow Analysis: ACT_PWSErrorConfig_Export

### Execution Steps:
1. **Search the Database for **EcoATM_PWSIntegration.PWSResponseConfig** using filter: { Show everything } (Call this list **$PWSResponseConfigList**)**
2. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Run another process: "Custom_Logging.SUB_Log_Info"**
4. **Create Object
      - Store the result in a new variable called **$MessageFileDocument****
5. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
6. **Download File**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
