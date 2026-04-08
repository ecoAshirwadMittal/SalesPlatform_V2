# Microflow Analysis: ACT_RMAItem_SalesDeclineAll

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Retrieve
      - Store the result in a new variable called **$RMAItemList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Run another process: "EcoATM_RMA.SUB_RMA_SetAllRMAItemsValid"**
8. **Run another process: "EcoATM_RMA.SUB_CalculateApprovedRMAValues"
      - Store the result in a new variable called **$Variable****
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
