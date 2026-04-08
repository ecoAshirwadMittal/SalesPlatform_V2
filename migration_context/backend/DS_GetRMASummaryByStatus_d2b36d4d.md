# Microflow Analysis: DS_GetRMASummaryByStatus

### Requirements (Inputs):
- **$RMAMasterHelper** (A record of type: EcoATM_RMA.RMAMasterHelper)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$RMAUiHelperList****
2. **Decision:** "Ui Helpers Availabe?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Take the list **$RMAUiHelperList**, perform a [Find] where: { $RMAMasterHelper }, and call the result **$RMAUiHelper_Selected****
5. **Decision:** "Status Selcted?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Take the list **$RMAUiHelperList**, perform a [Sort], and call the result **$RMAUiHelperList_Sorted****
7. **Permanently save **$undefined** to the database.**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
