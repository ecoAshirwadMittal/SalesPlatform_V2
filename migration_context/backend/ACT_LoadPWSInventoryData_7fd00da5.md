# Microflow Analysis: ACT_LoadPWSInventoryData

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "EcoATM_PWS.SUB_LoadPWSInventory_Snowflake"** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "EcoATM_PWS.SUB_LoadPWSInventory_Deposco"** ⚠️ *(This step has a safety catch if it fails)*
6. **Show Page**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
