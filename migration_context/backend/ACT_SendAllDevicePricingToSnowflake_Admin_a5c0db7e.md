# Microflow Analysis: ACT_SendAllDevicePricingToSnowflake_Admin

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Run another process: "EcoATM_PWS.DS_GetOrCreateMDMFuturePriceHelper"
      - Store the result in a new variable called **$MDMFuturePriceHelper****
5. **Decision:** "Future Date exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
6. **Show Message**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
