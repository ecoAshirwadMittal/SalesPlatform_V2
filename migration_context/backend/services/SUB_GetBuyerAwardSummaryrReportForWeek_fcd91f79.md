# Microflow Analysis: SUB_GetBuyerAwardSummaryrReportForWeek

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Decision:** "DAWeek not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Run another process: "EcoATM_Reports.SUB_LoadBuyerAwardsSummaryReport"
      - Store the result in a new variable called **$Message**** ⚠️ *(This step has a safety catch if it fails)*
4. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DAHelper.DisplayDA_DataGrid] to: "true
"
      - Change [EcoATM_DA.DAHelper_Week] to: "$Week
"
      - **Save:** This change will be saved to the database immediately.**
5. **Run another process: "Custom_Logging.SUB_Log_Info"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
