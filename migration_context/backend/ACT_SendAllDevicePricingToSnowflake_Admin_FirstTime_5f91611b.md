# Microflow Analysis: ACT_SendAllDevicePricingToSnowflake_Admin_FirstTime

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [IsActive] } (Call this list **$DeviceList_Active**)**
5. **Decision:** "DeviceList exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Show Message**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
