# Microflow Analysis: SUB_SetIsChangedBidData_DA

### Requirements (Inputs):
- **$DeviceBuyerList** (A record of type: EcoATM_DA.DeviceBuyer)
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Create List
      - Store the result in a new variable called **$BidDataList_Changed****
2. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
3. **Permanently save **$undefined** to the database.**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
