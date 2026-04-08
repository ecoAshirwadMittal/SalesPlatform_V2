# Microflow Analysis: ACT_GetCalibrationReport

### Requirements (Inputs):
- **$Week** (A record of type: EcoATM_MDM.Week)
- **$EBCalibrationQueryHelper** (A record of type: EcoATM_Reports.EBCalibrationQueryHelper)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Decision:** "exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
