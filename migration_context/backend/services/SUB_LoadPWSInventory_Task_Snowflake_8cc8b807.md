# Microflow Analysis: SUB_LoadPWSInventory_Task_Snowflake

### Requirements (Inputs):
- **$Limit** (A record of type: Object)
- **$Offset** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Execute Database Query
      - Store the result in a new variable called **$SFDeviceList**** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
7. **Java Action Call
      - Store the result in a new variable called **$Variable**** ⚠️ *(This step has a safety catch if it fails)*
8. **Aggregate List
      - Store the result in a new variable called **$DeviceCount****
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
