# Microflow Analysis: DS_GetOrCreateDAWeek

### Requirements (Inputs):
- **$DAHelper** (A record of type: EcoATM_DA.DAHelper)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **AuctionUI.Week** using filter: { [WeekEndDateTime>='[%CurrentDateTime%]']
 } (Call this list **$CurrentWeek**)**
3. **Decision:** "Is Day of the week Fri,Sat or Sunday?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
4. **Search the Database for **EcoATM_DA.DAWeek** using filter: { [EcoATM_DA.DAWeek_Week = $CurrentWeek]
 } (Call this list **$DAWeek**)**
5. **Decision:** "DAWeek exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Update the **$undefined** (Object):
      - Change [EcoATM_DA.DAHelper_DAWeek] to: "$DAWeek
"**
7. **Run another process: "EcoATM_DA.ACT_LoadDAData"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
