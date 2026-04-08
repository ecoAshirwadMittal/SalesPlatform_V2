# Microflow Analysis: ACT_PWSExpireDevicePriceHistoryInSnowflake

### Execution Steps:
1. **Run another process: "Eco_Core.ACT_FeatureFlag_RetrieveOrCreate"
      - Store the result in a new variable called **$FeatureFlagState****
2. **Decision:** "active?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Variable**
4. **Create Variable**
5. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
6. **Create Variable**
7. **Create Variable**
8. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
9. **Create Object
      - Store the result in a new variable called **$ArgumentACTING_USER****
10. **Change List**
11. **Create Variable**
12. **Run another process: "Custom_Logging.SUB_Log_Info"**
13. **Java Action Call
      - Store the result in a new variable called **$IsSuccess**** ⚠️ *(This step has a safety catch if it fails)*
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"
      - Store the result in a new variable called **$Log****
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
