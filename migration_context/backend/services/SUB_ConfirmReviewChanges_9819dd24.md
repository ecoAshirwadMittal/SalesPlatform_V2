# Microflow Analysis: SUB_ConfirmReviewChanges

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)
- **$DAWeek** (A record of type: EcoATM_DA.DAWeek)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Decision:** "DAWeek not empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [EcoATM_DA.DeviceBuyer_DAWeek = $DAWeek]
[IsChanged]
[not(EB)]
 } (Call this list **$ChangedDeviceBuyerList**)**
4. **Decision:** "DeviceBuyerList Not Empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Run another process: "EcoATM_DA.SUB_SetIsChangedBidData_DA"
      - Store the result in a new variable called **$BidDataList_Changed****
6. **Decision:** "BidDataList Not Empty?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
