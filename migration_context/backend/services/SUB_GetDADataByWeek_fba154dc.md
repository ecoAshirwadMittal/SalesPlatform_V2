# Microflow Analysis: SUB_GetDADataByWeek

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Decision:** "DAWeek not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Run another process: "EcoATM_DA.SUB_LoadDAData"
      - Store the result in a new variable called **$Message**** ⚠️ *(This step has a safety catch if it fails)*
4. **Decision:** "Success?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DAHelper.DisplayDA_DataGrid] to: "true
"
      - Change [EcoATM_DA.DAHelper_DAWeek] to: "$DAWeek
"
      - **Save:** This change will be saved to the database immediately.**
6. **Run another process: "Custom_Logging.SUB_Log_Info"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
