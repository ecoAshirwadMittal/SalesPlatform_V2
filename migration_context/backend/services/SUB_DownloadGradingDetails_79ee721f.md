# Microflow Analysis: SUB_DownloadGradingDetails

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Search the Database for **EcoATM_MDM.UserHelperGuide** using filter: { [Active]
[GuideType='PWS_Grading_Details']
 } (Call this list **$UserHelperGuide**)**
5. **Decision:** "UserHelperGuide  exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Download File**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
